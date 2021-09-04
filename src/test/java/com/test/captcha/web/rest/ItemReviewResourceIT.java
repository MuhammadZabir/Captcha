package com.test.captcha.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.test.captcha.IntegrationTest;
import com.test.captcha.domain.ItemReview;
import com.test.captcha.repository.ItemReviewRepository;
import com.test.captcha.repository.search.ItemReviewSearchRepository;
import com.test.captcha.service.EntityManager;
import com.test.captcha.service.dto.ItemReviewDTO;
import com.test.captcha.service.mapper.ItemReviewMapper;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link ItemReviewResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class ItemReviewResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;

    private static final LocalDate DEFAULT_REVIEW_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_REVIEW_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/item-reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/item-reviews";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ItemReviewRepository itemReviewRepository;

    @Autowired
    private ItemReviewMapper itemReviewMapper;

    /**
     * This repository is mocked in the com.test.captcha.repository.search test package.
     *
     * @see com.test.captcha.repository.search.ItemReviewSearchRepositoryMockConfiguration
     */
    @Autowired
    private ItemReviewSearchRepository mockItemReviewSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ItemReview itemReview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ItemReview createEntity(EntityManager em) {
        ItemReview itemReview = new ItemReview().description(DEFAULT_DESCRIPTION).rating(DEFAULT_RATING).reviewDate(DEFAULT_REVIEW_DATE);
        return itemReview;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ItemReview createUpdatedEntity(EntityManager em) {
        ItemReview itemReview = new ItemReview().description(UPDATED_DESCRIPTION).rating(UPDATED_RATING).reviewDate(UPDATED_REVIEW_DATE);
        return itemReview;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ItemReview.class).block();
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
        itemReview = createEntity(em);
    }

    @Test
    void createItemReview() throws Exception {
        int databaseSizeBeforeCreate = itemReviewRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockItemReviewSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the ItemReview
        ItemReviewDTO itemReviewDTO = itemReviewMapper.toDto(itemReview);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemReviewDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ItemReview in the database
        List<ItemReview> itemReviewList = itemReviewRepository.findAll().collectList().block();
        assertThat(itemReviewList).hasSize(databaseSizeBeforeCreate + 1);
        ItemReview testItemReview = itemReviewList.get(itemReviewList.size() - 1);
        assertThat(testItemReview.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testItemReview.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testItemReview.getReviewDate()).isEqualTo(DEFAULT_REVIEW_DATE);

        // Validate the ItemReview in Elasticsearch
        verify(mockItemReviewSearchRepository, times(1)).save(testItemReview);
    }

    @Test
    void createItemReviewWithExistingId() throws Exception {
        // Create the ItemReview with an existing ID
        itemReview.setId(1L);
        ItemReviewDTO itemReviewDTO = itemReviewMapper.toDto(itemReview);

        int databaseSizeBeforeCreate = itemReviewRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemReview in the database
        List<ItemReview> itemReviewList = itemReviewRepository.findAll().collectList().block();
        assertThat(itemReviewList).hasSize(databaseSizeBeforeCreate);

        // Validate the ItemReview in Elasticsearch
        verify(mockItemReviewSearchRepository, times(0)).save(itemReview);
    }

    @Test
    void getAllItemReviewsAsStream() {
        // Initialize the database
        itemReviewRepository.save(itemReview).block();

        List<ItemReview> itemReviewList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ItemReviewDTO.class)
            .getResponseBody()
            .map(itemReviewMapper::toEntity)
            .filter(itemReview::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(itemReviewList).isNotNull();
        assertThat(itemReviewList).hasSize(1);
        ItemReview testItemReview = itemReviewList.get(0);
        assertThat(testItemReview.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testItemReview.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testItemReview.getReviewDate()).isEqualTo(DEFAULT_REVIEW_DATE);
    }

    @Test
    void getAllItemReviews() {
        // Initialize the database
        itemReviewRepository.save(itemReview).block();

        // Get all the itemReviewList
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
            .value(hasItem(itemReview.getId().intValue()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].reviewDate")
            .value(hasItem(DEFAULT_REVIEW_DATE.toString()));
    }

    @Test
    void getItemReview() {
        // Initialize the database
        itemReviewRepository.save(itemReview).block();

        // Get the itemReview
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, itemReview.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(itemReview.getId().intValue()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING))
            .jsonPath("$.reviewDate")
            .value(is(DEFAULT_REVIEW_DATE.toString()));
    }

    @Test
    void getNonExistingItemReview() {
        // Get the itemReview
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewItemReview() throws Exception {
        // Configure the mock search repository
        when(mockItemReviewSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        itemReviewRepository.save(itemReview).block();

        int databaseSizeBeforeUpdate = itemReviewRepository.findAll().collectList().block().size();

        // Update the itemReview
        ItemReview updatedItemReview = itemReviewRepository.findById(itemReview.getId()).block();
        updatedItemReview.description(UPDATED_DESCRIPTION).rating(UPDATED_RATING).reviewDate(UPDATED_REVIEW_DATE);
        ItemReviewDTO itemReviewDTO = itemReviewMapper.toDto(updatedItemReview);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, itemReviewDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemReviewDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ItemReview in the database
        List<ItemReview> itemReviewList = itemReviewRepository.findAll().collectList().block();
        assertThat(itemReviewList).hasSize(databaseSizeBeforeUpdate);
        ItemReview testItemReview = itemReviewList.get(itemReviewList.size() - 1);
        assertThat(testItemReview.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testItemReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testItemReview.getReviewDate()).isEqualTo(UPDATED_REVIEW_DATE);

        // Validate the ItemReview in Elasticsearch
        verify(mockItemReviewSearchRepository).save(testItemReview);
    }

    @Test
    void putNonExistingItemReview() throws Exception {
        int databaseSizeBeforeUpdate = itemReviewRepository.findAll().collectList().block().size();
        itemReview.setId(count.incrementAndGet());

        // Create the ItemReview
        ItemReviewDTO itemReviewDTO = itemReviewMapper.toDto(itemReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, itemReviewDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemReview in the database
        List<ItemReview> itemReviewList = itemReviewRepository.findAll().collectList().block();
        assertThat(itemReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ItemReview in Elasticsearch
        verify(mockItemReviewSearchRepository, times(0)).save(itemReview);
    }

    @Test
    void putWithIdMismatchItemReview() throws Exception {
        int databaseSizeBeforeUpdate = itemReviewRepository.findAll().collectList().block().size();
        itemReview.setId(count.incrementAndGet());

        // Create the ItemReview
        ItemReviewDTO itemReviewDTO = itemReviewMapper.toDto(itemReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemReview in the database
        List<ItemReview> itemReviewList = itemReviewRepository.findAll().collectList().block();
        assertThat(itemReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ItemReview in Elasticsearch
        verify(mockItemReviewSearchRepository, times(0)).save(itemReview);
    }

    @Test
    void putWithMissingIdPathParamItemReview() throws Exception {
        int databaseSizeBeforeUpdate = itemReviewRepository.findAll().collectList().block().size();
        itemReview.setId(count.incrementAndGet());

        // Create the ItemReview
        ItemReviewDTO itemReviewDTO = itemReviewMapper.toDto(itemReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemReviewDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ItemReview in the database
        List<ItemReview> itemReviewList = itemReviewRepository.findAll().collectList().block();
        assertThat(itemReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ItemReview in Elasticsearch
        verify(mockItemReviewSearchRepository, times(0)).save(itemReview);
    }

    @Test
    void partialUpdateItemReviewWithPatch() throws Exception {
        // Initialize the database
        itemReviewRepository.save(itemReview).block();

        int databaseSizeBeforeUpdate = itemReviewRepository.findAll().collectList().block().size();

        // Update the itemReview using partial update
        ItemReview partialUpdatedItemReview = new ItemReview();
        partialUpdatedItemReview.setId(itemReview.getId());

        partialUpdatedItemReview.rating(UPDATED_RATING);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedItemReview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedItemReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ItemReview in the database
        List<ItemReview> itemReviewList = itemReviewRepository.findAll().collectList().block();
        assertThat(itemReviewList).hasSize(databaseSizeBeforeUpdate);
        ItemReview testItemReview = itemReviewList.get(itemReviewList.size() - 1);
        assertThat(testItemReview.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testItemReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testItemReview.getReviewDate()).isEqualTo(DEFAULT_REVIEW_DATE);
    }

    @Test
    void fullUpdateItemReviewWithPatch() throws Exception {
        // Initialize the database
        itemReviewRepository.save(itemReview).block();

        int databaseSizeBeforeUpdate = itemReviewRepository.findAll().collectList().block().size();

        // Update the itemReview using partial update
        ItemReview partialUpdatedItemReview = new ItemReview();
        partialUpdatedItemReview.setId(itemReview.getId());

        partialUpdatedItemReview.description(UPDATED_DESCRIPTION).rating(UPDATED_RATING).reviewDate(UPDATED_REVIEW_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedItemReview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedItemReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ItemReview in the database
        List<ItemReview> itemReviewList = itemReviewRepository.findAll().collectList().block();
        assertThat(itemReviewList).hasSize(databaseSizeBeforeUpdate);
        ItemReview testItemReview = itemReviewList.get(itemReviewList.size() - 1);
        assertThat(testItemReview.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testItemReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testItemReview.getReviewDate()).isEqualTo(UPDATED_REVIEW_DATE);
    }

    @Test
    void patchNonExistingItemReview() throws Exception {
        int databaseSizeBeforeUpdate = itemReviewRepository.findAll().collectList().block().size();
        itemReview.setId(count.incrementAndGet());

        // Create the ItemReview
        ItemReviewDTO itemReviewDTO = itemReviewMapper.toDto(itemReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, itemReviewDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemReview in the database
        List<ItemReview> itemReviewList = itemReviewRepository.findAll().collectList().block();
        assertThat(itemReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ItemReview in Elasticsearch
        verify(mockItemReviewSearchRepository, times(0)).save(itemReview);
    }

    @Test
    void patchWithIdMismatchItemReview() throws Exception {
        int databaseSizeBeforeUpdate = itemReviewRepository.findAll().collectList().block().size();
        itemReview.setId(count.incrementAndGet());

        // Create the ItemReview
        ItemReviewDTO itemReviewDTO = itemReviewMapper.toDto(itemReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ItemReview in the database
        List<ItemReview> itemReviewList = itemReviewRepository.findAll().collectList().block();
        assertThat(itemReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ItemReview in Elasticsearch
        verify(mockItemReviewSearchRepository, times(0)).save(itemReview);
    }

    @Test
    void patchWithMissingIdPathParamItemReview() throws Exception {
        int databaseSizeBeforeUpdate = itemReviewRepository.findAll().collectList().block().size();
        itemReview.setId(count.incrementAndGet());

        // Create the ItemReview
        ItemReviewDTO itemReviewDTO = itemReviewMapper.toDto(itemReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(itemReviewDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ItemReview in the database
        List<ItemReview> itemReviewList = itemReviewRepository.findAll().collectList().block();
        assertThat(itemReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ItemReview in Elasticsearch
        verify(mockItemReviewSearchRepository, times(0)).save(itemReview);
    }

    @Test
    void deleteItemReview() {
        // Configure the mock search repository
        when(mockItemReviewSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockItemReviewSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        itemReviewRepository.save(itemReview).block();

        int databaseSizeBeforeDelete = itemReviewRepository.findAll().collectList().block().size();

        // Delete the itemReview
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, itemReview.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ItemReview> itemReviewList = itemReviewRepository.findAll().collectList().block();
        assertThat(itemReviewList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ItemReview in Elasticsearch
        verify(mockItemReviewSearchRepository, times(1)).deleteById(itemReview.getId());
    }

    @Test
    void searchItemReview() {
        // Configure the mock search repository
        when(mockItemReviewSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        itemReviewRepository.save(itemReview).block();
        when(mockItemReviewSearchRepository.search("id:" + itemReview.getId())).thenReturn(Flux.just(itemReview));

        // Search the itemReview
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + itemReview.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(itemReview.getId().intValue()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].reviewDate")
            .value(hasItem(DEFAULT_REVIEW_DATE.toString()));
    }
}
