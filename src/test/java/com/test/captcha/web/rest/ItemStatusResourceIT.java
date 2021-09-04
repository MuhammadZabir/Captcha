package com.test.captcha.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.test.captcha.IntegrationTest;
import com.test.captcha.domain.ItemStatus;
import com.test.captcha.domain.enumeration.AvailabilityStatus;
import com.test.captcha.repository.ItemStatusRepository;
import com.test.captcha.repository.search.ItemStatusSearchRepository;
import com.test.captcha.service.EntityManager;
import com.test.captcha.service.dto.ItemStatusDTO;
import com.test.captcha.service.mapper.ItemStatusMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link ItemStatusResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class ItemStatusResourceIT {

    private static final Integer DEFAULT_AMOUNT_AVAILABLE = 1;
    private static final Integer UPDATED_AMOUNT_AVAILABLE = 2;

    private static final Integer DEFAULT_AMOUNT_SOLD = 1;
    private static final Integer UPDATED_AMOUNT_SOLD = 2;

    private static final AvailabilityStatus DEFAULT_AVAILABILITY_STATUS = AvailabilityStatus.AVAILABLE;
    private static final AvailabilityStatus UPDATED_AVAILABILITY_STATUS = AvailabilityStatus.SOLD_OUT;

    private static final String ENTITY_API_URL = "/api/item-statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/item-statuses";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ItemStatusRepository itemStatusRepository;

    @Autowired
    private ItemStatusMapper itemStatusMapper;

    /**
     * This repository is mocked in the com.test.captcha.repository.search test package.
     *
     * @see com.test.captcha.repository.search.ItemStatusSearchRepositoryMockConfiguration
     */
    @Autowired
    private ItemStatusSearchRepository mockItemStatusSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ItemStatus itemStatus;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ItemStatus createEntity(EntityManager em) {
        ItemStatus itemStatus = new ItemStatus()
            .amountAvailable(DEFAULT_AMOUNT_AVAILABLE)
            .amountSold(DEFAULT_AMOUNT_SOLD)
            .availabilityStatus(DEFAULT_AVAILABILITY_STATUS);
        return itemStatus;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ItemStatus createUpdatedEntity(EntityManager em) {
        ItemStatus itemStatus = new ItemStatus()
            .amountAvailable(UPDATED_AMOUNT_AVAILABLE)
            .amountSold(UPDATED_AMOUNT_SOLD)
            .availabilityStatus(UPDATED_AVAILABILITY_STATUS);
        return itemStatus;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ItemStatus.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        itemStatus = createEntity(em);
    }

    @Test
    void createItemStatus() throws Exception {
        int databaseSizeBeforeCreate = itemStatusRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockItemStatusSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the ItemStatus
        ItemStatusDTO itemStatusDTO = itemStatusMapper.toDto(itemStatus);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemStatusDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ItemStatus in the database
        List<ItemStatus> itemStatusList = itemStatusRepository.findAll().collectList().block();
        assertThat(itemStatusList).hasSize(databaseSizeBeforeCreate + 1);
        ItemStatus testItemStatus = itemStatusList.get(itemStatusList.size() - 1);
        assertThat(testItemStatus.getAmountAvailable()).isEqualTo(DEFAULT_AMOUNT_AVAILABLE);
        assertThat(testItemStatus.getAmountSold()).isEqualTo(DEFAULT_AMOUNT_SOLD);
        assertThat(testItemStatus.getAvailabilityStatus()).isEqualTo(DEFAULT_AVAILABILITY_STATUS);

        // Validate the ItemStatus in Elasticsearch
        verify(mockItemStatusSearchRepository, times(1)).save(testItemStatus);
    }

    @Test
    void createItemStatusWithExistingId() throws Exception {
        // Create the ItemStatus with an existing ID
        itemStatus.setId(1L);
        ItemStatusDTO itemStatusDTO = itemStatusMapper.toDto(itemStatus);

        int databaseSizeBeforeCreate = itemStatusRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemStatusDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemStatus in the database
        List<ItemStatus> itemStatusList = itemStatusRepository.findAll().collectList().block();
        assertThat(itemStatusList).hasSize(databaseSizeBeforeCreate);

        // Validate the ItemStatus in Elasticsearch
        verify(mockItemStatusSearchRepository, times(0)).save(itemStatus);
    }

    @Test
    void getAllItemStatusesAsStream() {
        // Initialize the database
        itemStatusRepository.save(itemStatus).block();

        List<ItemStatus> itemStatusList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ItemStatusDTO.class)
            .getResponseBody()
            .map(itemStatusMapper::toEntity)
            .filter(itemStatus::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(itemStatusList).isNotNull();
        assertThat(itemStatusList).hasSize(1);
        ItemStatus testItemStatus = itemStatusList.get(0);
        assertThat(testItemStatus.getAmountAvailable()).isEqualTo(DEFAULT_AMOUNT_AVAILABLE);
        assertThat(testItemStatus.getAmountSold()).isEqualTo(DEFAULT_AMOUNT_SOLD);
        assertThat(testItemStatus.getAvailabilityStatus()).isEqualTo(DEFAULT_AVAILABILITY_STATUS);
    }

    @Test
    void getAllItemStatuses() {
        // Initialize the database
        itemStatusRepository.save(itemStatus).block();

        // Get all the itemStatusList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(itemStatus.getId().intValue()))
            .jsonPath("$.[*].amountAvailable")
            .value(hasItem(DEFAULT_AMOUNT_AVAILABLE))
            .jsonPath("$.[*].amountSold")
            .value(hasItem(DEFAULT_AMOUNT_SOLD))
            .jsonPath("$.[*].availabilityStatus")
            .value(hasItem(DEFAULT_AVAILABILITY_STATUS.toString()));
    }

    @Test
    void getItemStatus() {
        // Initialize the database
        itemStatusRepository.save(itemStatus).block();

        // Get the itemStatus
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, itemStatus.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(itemStatus.getId().intValue()))
            .jsonPath("$.amountAvailable")
            .value(is(DEFAULT_AMOUNT_AVAILABLE))
            .jsonPath("$.amountSold")
            .value(is(DEFAULT_AMOUNT_SOLD))
            .jsonPath("$.availabilityStatus")
            .value(is(DEFAULT_AVAILABILITY_STATUS.toString()));
    }

    @Test
    void getNonExistingItemStatus() {
        // Get the itemStatus
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewItemStatus() throws Exception {
        // Configure the mock search repository
        when(mockItemStatusSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        itemStatusRepository.save(itemStatus).block();

        int databaseSizeBeforeUpdate = itemStatusRepository.findAll().collectList().block().size();

        // Update the itemStatus
        ItemStatus updatedItemStatus = itemStatusRepository.findById(itemStatus.getId()).block();
        updatedItemStatus
            .amountAvailable(UPDATED_AMOUNT_AVAILABLE)
            .amountSold(UPDATED_AMOUNT_SOLD)
            .availabilityStatus(UPDATED_AVAILABILITY_STATUS);
        ItemStatusDTO itemStatusDTO = itemStatusMapper.toDto(updatedItemStatus);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, itemStatusDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemStatusDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ItemStatus in the database
        List<ItemStatus> itemStatusList = itemStatusRepository.findAll().collectList().block();
        assertThat(itemStatusList).hasSize(databaseSizeBeforeUpdate);
        ItemStatus testItemStatus = itemStatusList.get(itemStatusList.size() - 1);
        assertThat(testItemStatus.getAmountAvailable()).isEqualTo(UPDATED_AMOUNT_AVAILABLE);
        assertThat(testItemStatus.getAmountSold()).isEqualTo(UPDATED_AMOUNT_SOLD);
        assertThat(testItemStatus.getAvailabilityStatus()).isEqualTo(UPDATED_AVAILABILITY_STATUS);

        // Validate the ItemStatus in Elasticsearch
        verify(mockItemStatusSearchRepository).save(testItemStatus);
    }

    @Test
    void putNonExistingItemStatus() throws Exception {
        int databaseSizeBeforeUpdate = itemStatusRepository.findAll().collectList().block().size();
        itemStatus.setId(count.incrementAndGet());

        // Create the ItemStatus
        ItemStatusDTO itemStatusDTO = itemStatusMapper.toDto(itemStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, itemStatusDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemStatusDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemStatus in the database
        List<ItemStatus> itemStatusList = itemStatusRepository.findAll().collectList().block();
        assertThat(itemStatusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ItemStatus in Elasticsearch
        verify(mockItemStatusSearchRepository, times(0)).save(itemStatus);
    }

    @Test
    void putWithIdMismatchItemStatus() throws Exception {
        int databaseSizeBeforeUpdate = itemStatusRepository.findAll().collectList().block().size();
        itemStatus.setId(count.incrementAndGet());

        // Create the ItemStatus
        ItemStatusDTO itemStatusDTO = itemStatusMapper.toDto(itemStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemStatusDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemStatus in the database
        List<ItemStatus> itemStatusList = itemStatusRepository.findAll().collectList().block();
        assertThat(itemStatusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ItemStatus in Elasticsearch
        verify(mockItemStatusSearchRepository, times(0)).save(itemStatus);
    }

    @Test
    void putWithMissingIdPathParamItemStatus() throws Exception {
        int databaseSizeBeforeUpdate = itemStatusRepository.findAll().collectList().block().size();
        itemStatus.setId(count.incrementAndGet());

        // Create the ItemStatus
        ItemStatusDTO itemStatusDTO = itemStatusMapper.toDto(itemStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemStatusDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ItemStatus in the database
        List<ItemStatus> itemStatusList = itemStatusRepository.findAll().collectList().block();
        assertThat(itemStatusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ItemStatus in Elasticsearch
        verify(mockItemStatusSearchRepository, times(0)).save(itemStatus);
    }

    @Test
    void partialUpdateItemStatusWithPatch() throws Exception {
        // Initialize the database
        itemStatusRepository.save(itemStatus).block();

        int databaseSizeBeforeUpdate = itemStatusRepository.findAll().collectList().block().size();

        // Update the itemStatus using partial update
        ItemStatus partialUpdatedItemStatus = new ItemStatus();
        partialUpdatedItemStatus.setId(itemStatus.getId());

        partialUpdatedItemStatus.availabilityStatus(UPDATED_AVAILABILITY_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedItemStatus.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedItemStatus))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ItemStatus in the database
        List<ItemStatus> itemStatusList = itemStatusRepository.findAll().collectList().block();
        assertThat(itemStatusList).hasSize(databaseSizeBeforeUpdate);
        ItemStatus testItemStatus = itemStatusList.get(itemStatusList.size() - 1);
        assertThat(testItemStatus.getAmountAvailable()).isEqualTo(DEFAULT_AMOUNT_AVAILABLE);
        assertThat(testItemStatus.getAmountSold()).isEqualTo(DEFAULT_AMOUNT_SOLD);
        assertThat(testItemStatus.getAvailabilityStatus()).isEqualTo(UPDATED_AVAILABILITY_STATUS);
    }

    @Test
    void fullUpdateItemStatusWithPatch() throws Exception {
        // Initialize the database
        itemStatusRepository.save(itemStatus).block();

        int databaseSizeBeforeUpdate = itemStatusRepository.findAll().collectList().block().size();

        // Update the itemStatus using partial update
        ItemStatus partialUpdatedItemStatus = new ItemStatus();
        partialUpdatedItemStatus.setId(itemStatus.getId());

        partialUpdatedItemStatus
            .amountAvailable(UPDATED_AMOUNT_AVAILABLE)
            .amountSold(UPDATED_AMOUNT_SOLD)
            .availabilityStatus(UPDATED_AVAILABILITY_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedItemStatus.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedItemStatus))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ItemStatus in the database
        List<ItemStatus> itemStatusList = itemStatusRepository.findAll().collectList().block();
        assertThat(itemStatusList).hasSize(databaseSizeBeforeUpdate);
        ItemStatus testItemStatus = itemStatusList.get(itemStatusList.size() - 1);
        assertThat(testItemStatus.getAmountAvailable()).isEqualTo(UPDATED_AMOUNT_AVAILABLE);
        assertThat(testItemStatus.getAmountSold()).isEqualTo(UPDATED_AMOUNT_SOLD);
        assertThat(testItemStatus.getAvailabilityStatus()).isEqualTo(UPDATED_AVAILABILITY_STATUS);
    }

    @Test
    void patchNonExistingItemStatus() throws Exception {
        int databaseSizeBeforeUpdate = itemStatusRepository.findAll().collectList().block().size();
        itemStatus.setId(count.incrementAndGet());

        // Create the ItemStatus
        ItemStatusDTO itemStatusDTO = itemStatusMapper.toDto(itemStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, itemStatusDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemStatusDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemStatus in the database
        List<ItemStatus> itemStatusList = itemStatusRepository.findAll().collectList().block();
        assertThat(itemStatusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ItemStatus in Elasticsearch
        verify(mockItemStatusSearchRepository, times(0)).save(itemStatus);
    }

    @Test
    void patchWithIdMismatchItemStatus() throws Exception {
        int databaseSizeBeforeUpdate = itemStatusRepository.findAll().collectList().block().size();
        itemStatus.setId(count.incrementAndGet());

        // Create the ItemStatus
        ItemStatusDTO itemStatusDTO = itemStatusMapper.toDto(itemStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemStatusDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemStatus in the database
        List<ItemStatus> itemStatusList = itemStatusRepository.findAll().collectList().block();
        assertThat(itemStatusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ItemStatus in Elasticsearch
        verify(mockItemStatusSearchRepository, times(0)).save(itemStatus);
    }

    @Test
    void patchWithMissingIdPathParamItemStatus() throws Exception {
        int databaseSizeBeforeUpdate = itemStatusRepository.findAll().collectList().block().size();
        itemStatus.setId(count.incrementAndGet());

        // Create the ItemStatus
        ItemStatusDTO itemStatusDTO = itemStatusMapper.toDto(itemStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemStatusDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ItemStatus in the database
        List<ItemStatus> itemStatusList = itemStatusRepository.findAll().collectList().block();
        assertThat(itemStatusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ItemStatus in Elasticsearch
        verify(mockItemStatusSearchRepository, times(0)).save(itemStatus);
    }

    @Test
    void deleteItemStatus() {
        // Configure the mock search repository
        when(mockItemStatusSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockItemStatusSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        itemStatusRepository.save(itemStatus).block();

        int databaseSizeBeforeDelete = itemStatusRepository.findAll().collectList().block().size();

        // Delete the itemStatus
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, itemStatus.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ItemStatus> itemStatusList = itemStatusRepository.findAll().collectList().block();
        assertThat(itemStatusList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ItemStatus in Elasticsearch
        verify(mockItemStatusSearchRepository, times(1)).deleteById(itemStatus.getId());
    }

    @Test
    void searchItemStatus() {
        // Configure the mock search repository
        when(mockItemStatusSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        itemStatusRepository.save(itemStatus).block();
        when(mockItemStatusSearchRepository.search("id:" + itemStatus.getId())).thenReturn(Flux.just(itemStatus));

        // Search the itemStatus
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + itemStatus.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(itemStatus.getId().intValue()))
            .jsonPath("$.[*].amountAvailable")
            .value(hasItem(DEFAULT_AMOUNT_AVAILABLE))
            .jsonPath("$.[*].amountSold")
            .value(hasItem(DEFAULT_AMOUNT_SOLD))
            .jsonPath("$.[*].availabilityStatus")
            .value(hasItem(DEFAULT_AVAILABILITY_STATUS.toString()));
    }
}
