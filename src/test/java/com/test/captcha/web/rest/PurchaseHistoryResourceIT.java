package com.test.captcha.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.test.captcha.IntegrationTest;
import com.test.captcha.domain.PurchaseHistory;
import com.test.captcha.domain.enumeration.PaymentStatus;
import com.test.captcha.repository.PurchaseHistoryRepository;
import com.test.captcha.repository.search.PurchaseHistorySearchRepository;
import com.test.captcha.service.EntityManager;
import com.test.captcha.service.dto.PurchaseHistoryDTO;
import com.test.captcha.service.mapper.PurchaseHistoryMapper;
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
 * Integration tests for the {@link PurchaseHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class PurchaseHistoryResourceIT {

    private static final LocalDate DEFAULT_PURCHASE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PURCHASE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_SHIPPING_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SHIPPING_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_BILLING_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_BILLING_ADDRESS = "BBBBBBBBBB";

    private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.PAID;
    private static final PaymentStatus UPDATED_PAYMENT_STATUS = PaymentStatus.PENDING;

    private static final String ENTITY_API_URL = "/api/purchase-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/purchase-histories";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @Autowired
    private PurchaseHistoryMapper purchaseHistoryMapper;

    /**
     * This repository is mocked in the com.test.captcha.repository.search test package.
     *
     * @see com.test.captcha.repository.search.PurchaseHistorySearchRepositoryMockConfiguration
     */
    @Autowired
    private PurchaseHistorySearchRepository mockPurchaseHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PurchaseHistory purchaseHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseHistory createEntity(EntityManager em) {
        PurchaseHistory purchaseHistory = new PurchaseHistory()
            .purchaseDate(DEFAULT_PURCHASE_DATE)
            .shippingDate(DEFAULT_SHIPPING_DATE)
            .billingAddress(DEFAULT_BILLING_ADDRESS)
            .paymentStatus(DEFAULT_PAYMENT_STATUS);
        return purchaseHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseHistory createUpdatedEntity(EntityManager em) {
        PurchaseHistory purchaseHistory = new PurchaseHistory()
            .purchaseDate(UPDATED_PURCHASE_DATE)
            .shippingDate(UPDATED_SHIPPING_DATE)
            .billingAddress(UPDATED_BILLING_ADDRESS)
            .paymentStatus(UPDATED_PAYMENT_STATUS);
        return purchaseHistory;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PurchaseHistory.class).block();
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
        purchaseHistory = createEntity(em);
    }

    @Test
    void createPurchaseHistory() throws Exception {
        int databaseSizeBeforeCreate = purchaseHistoryRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockPurchaseHistorySearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the PurchaseHistory
        PurchaseHistoryDTO purchaseHistoryDTO = purchaseHistoryMapper.toDto(purchaseHistory);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(purchaseHistoryDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PurchaseHistory in the database
        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findAll().collectList().block();
        assertThat(purchaseHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        PurchaseHistory testPurchaseHistory = purchaseHistoryList.get(purchaseHistoryList.size() - 1);
        assertThat(testPurchaseHistory.getPurchaseDate()).isEqualTo(DEFAULT_PURCHASE_DATE);
        assertThat(testPurchaseHistory.getShippingDate()).isEqualTo(DEFAULT_SHIPPING_DATE);
        assertThat(testPurchaseHistory.getBillingAddress()).isEqualTo(DEFAULT_BILLING_ADDRESS);
        assertThat(testPurchaseHistory.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);

        // Validate the PurchaseHistory in Elasticsearch
        verify(mockPurchaseHistorySearchRepository, times(1)).save(testPurchaseHistory);
    }

    @Test
    void createPurchaseHistoryWithExistingId() throws Exception {
        // Create the PurchaseHistory with an existing ID
        purchaseHistory.setId(1L);
        PurchaseHistoryDTO purchaseHistoryDTO = purchaseHistoryMapper.toDto(purchaseHistory);

        int databaseSizeBeforeCreate = purchaseHistoryRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(purchaseHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PurchaseHistory in the database
        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findAll().collectList().block();
        assertThat(purchaseHistoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the PurchaseHistory in Elasticsearch
        verify(mockPurchaseHistorySearchRepository, times(0)).save(purchaseHistory);
    }

    @Test
    void getAllPurchaseHistoriesAsStream() {
        // Initialize the database
        purchaseHistoryRepository.save(purchaseHistory).block();

        List<PurchaseHistory> purchaseHistoryList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PurchaseHistoryDTO.class)
            .getResponseBody()
            .map(purchaseHistoryMapper::toEntity)
            .filter(purchaseHistory::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(purchaseHistoryList).isNotNull();
        assertThat(purchaseHistoryList).hasSize(1);
        PurchaseHistory testPurchaseHistory = purchaseHistoryList.get(0);
        assertThat(testPurchaseHistory.getPurchaseDate()).isEqualTo(DEFAULT_PURCHASE_DATE);
        assertThat(testPurchaseHistory.getShippingDate()).isEqualTo(DEFAULT_SHIPPING_DATE);
        assertThat(testPurchaseHistory.getBillingAddress()).isEqualTo(DEFAULT_BILLING_ADDRESS);
        assertThat(testPurchaseHistory.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
    }

    @Test
    void getAllPurchaseHistories() {
        // Initialize the database
        purchaseHistoryRepository.save(purchaseHistory).block();

        // Get all the purchaseHistoryList
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
            .value(hasItem(purchaseHistory.getId().intValue()))
            .jsonPath("$.[*].purchaseDate")
            .value(hasItem(DEFAULT_PURCHASE_DATE.toString()))
            .jsonPath("$.[*].shippingDate")
            .value(hasItem(DEFAULT_SHIPPING_DATE.toString()))
            .jsonPath("$.[*].billingAddress")
            .value(hasItem(DEFAULT_BILLING_ADDRESS))
            .jsonPath("$.[*].paymentStatus")
            .value(hasItem(DEFAULT_PAYMENT_STATUS.toString()));
    }

    @Test
    void getPurchaseHistory() {
        // Initialize the database
        purchaseHistoryRepository.save(purchaseHistory).block();

        // Get the purchaseHistory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, purchaseHistory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(purchaseHistory.getId().intValue()))
            .jsonPath("$.purchaseDate")
            .value(is(DEFAULT_PURCHASE_DATE.toString()))
            .jsonPath("$.shippingDate")
            .value(is(DEFAULT_SHIPPING_DATE.toString()))
            .jsonPath("$.billingAddress")
            .value(is(DEFAULT_BILLING_ADDRESS))
            .jsonPath("$.paymentStatus")
            .value(is(DEFAULT_PAYMENT_STATUS.toString()));
    }

    @Test
    void getNonExistingPurchaseHistory() {
        // Get the purchaseHistory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPurchaseHistory() throws Exception {
        // Configure the mock search repository
        when(mockPurchaseHistorySearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        purchaseHistoryRepository.save(purchaseHistory).block();

        int databaseSizeBeforeUpdate = purchaseHistoryRepository.findAll().collectList().block().size();

        // Update the purchaseHistory
        PurchaseHistory updatedPurchaseHistory = purchaseHistoryRepository.findById(purchaseHistory.getId()).block();
        updatedPurchaseHistory
            .purchaseDate(UPDATED_PURCHASE_DATE)
            .shippingDate(UPDATED_SHIPPING_DATE)
            .billingAddress(UPDATED_BILLING_ADDRESS)
            .paymentStatus(UPDATED_PAYMENT_STATUS);
        PurchaseHistoryDTO purchaseHistoryDTO = purchaseHistoryMapper.toDto(updatedPurchaseHistory);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, purchaseHistoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(purchaseHistoryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PurchaseHistory in the database
        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findAll().collectList().block();
        assertThat(purchaseHistoryList).hasSize(databaseSizeBeforeUpdate);
        PurchaseHistory testPurchaseHistory = purchaseHistoryList.get(purchaseHistoryList.size() - 1);
        assertThat(testPurchaseHistory.getPurchaseDate()).isEqualTo(UPDATED_PURCHASE_DATE);
        assertThat(testPurchaseHistory.getShippingDate()).isEqualTo(UPDATED_SHIPPING_DATE);
        assertThat(testPurchaseHistory.getBillingAddress()).isEqualTo(UPDATED_BILLING_ADDRESS);
        assertThat(testPurchaseHistory.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);

        // Validate the PurchaseHistory in Elasticsearch
        verify(mockPurchaseHistorySearchRepository).save(testPurchaseHistory);
    }

    @Test
    void putNonExistingPurchaseHistory() throws Exception {
        int databaseSizeBeforeUpdate = purchaseHistoryRepository.findAll().collectList().block().size();
        purchaseHistory.setId(count.incrementAndGet());

        // Create the PurchaseHistory
        PurchaseHistoryDTO purchaseHistoryDTO = purchaseHistoryMapper.toDto(purchaseHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, purchaseHistoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(purchaseHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PurchaseHistory in the database
        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findAll().collectList().block();
        assertThat(purchaseHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PurchaseHistory in Elasticsearch
        verify(mockPurchaseHistorySearchRepository, times(0)).save(purchaseHistory);
    }

    @Test
    void putWithIdMismatchPurchaseHistory() throws Exception {
        int databaseSizeBeforeUpdate = purchaseHistoryRepository.findAll().collectList().block().size();
        purchaseHistory.setId(count.incrementAndGet());

        // Create the PurchaseHistory
        PurchaseHistoryDTO purchaseHistoryDTO = purchaseHistoryMapper.toDto(purchaseHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(purchaseHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PurchaseHistory in the database
        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findAll().collectList().block();
        assertThat(purchaseHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PurchaseHistory in Elasticsearch
        verify(mockPurchaseHistorySearchRepository, times(0)).save(purchaseHistory);
    }

    @Test
    void putWithMissingIdPathParamPurchaseHistory() throws Exception {
        int databaseSizeBeforeUpdate = purchaseHistoryRepository.findAll().collectList().block().size();
        purchaseHistory.setId(count.incrementAndGet());

        // Create the PurchaseHistory
        PurchaseHistoryDTO purchaseHistoryDTO = purchaseHistoryMapper.toDto(purchaseHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(purchaseHistoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PurchaseHistory in the database
        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findAll().collectList().block();
        assertThat(purchaseHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PurchaseHistory in Elasticsearch
        verify(mockPurchaseHistorySearchRepository, times(0)).save(purchaseHistory);
    }

    @Test
    void partialUpdatePurchaseHistoryWithPatch() throws Exception {
        // Initialize the database
        purchaseHistoryRepository.save(purchaseHistory).block();

        int databaseSizeBeforeUpdate = purchaseHistoryRepository.findAll().collectList().block().size();

        // Update the purchaseHistory using partial update
        PurchaseHistory partialUpdatedPurchaseHistory = new PurchaseHistory();
        partialUpdatedPurchaseHistory.setId(purchaseHistory.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPurchaseHistory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPurchaseHistory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PurchaseHistory in the database
        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findAll().collectList().block();
        assertThat(purchaseHistoryList).hasSize(databaseSizeBeforeUpdate);
        PurchaseHistory testPurchaseHistory = purchaseHistoryList.get(purchaseHistoryList.size() - 1);
        assertThat(testPurchaseHistory.getPurchaseDate()).isEqualTo(DEFAULT_PURCHASE_DATE);
        assertThat(testPurchaseHistory.getShippingDate()).isEqualTo(DEFAULT_SHIPPING_DATE);
        assertThat(testPurchaseHistory.getBillingAddress()).isEqualTo(DEFAULT_BILLING_ADDRESS);
        assertThat(testPurchaseHistory.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
    }

    @Test
    void fullUpdatePurchaseHistoryWithPatch() throws Exception {
        // Initialize the database
        purchaseHistoryRepository.save(purchaseHistory).block();

        int databaseSizeBeforeUpdate = purchaseHistoryRepository.findAll().collectList().block().size();

        // Update the purchaseHistory using partial update
        PurchaseHistory partialUpdatedPurchaseHistory = new PurchaseHistory();
        partialUpdatedPurchaseHistory.setId(purchaseHistory.getId());

        partialUpdatedPurchaseHistory
            .purchaseDate(UPDATED_PURCHASE_DATE)
            .shippingDate(UPDATED_SHIPPING_DATE)
            .billingAddress(UPDATED_BILLING_ADDRESS)
            .paymentStatus(UPDATED_PAYMENT_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPurchaseHistory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPurchaseHistory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PurchaseHistory in the database
        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findAll().collectList().block();
        assertThat(purchaseHistoryList).hasSize(databaseSizeBeforeUpdate);
        PurchaseHistory testPurchaseHistory = purchaseHistoryList.get(purchaseHistoryList.size() - 1);
        assertThat(testPurchaseHistory.getPurchaseDate()).isEqualTo(UPDATED_PURCHASE_DATE);
        assertThat(testPurchaseHistory.getShippingDate()).isEqualTo(UPDATED_SHIPPING_DATE);
        assertThat(testPurchaseHistory.getBillingAddress()).isEqualTo(UPDATED_BILLING_ADDRESS);
        assertThat(testPurchaseHistory.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
    }

    @Test
    void patchNonExistingPurchaseHistory() throws Exception {
        int databaseSizeBeforeUpdate = purchaseHistoryRepository.findAll().collectList().block().size();
        purchaseHistory.setId(count.incrementAndGet());

        // Create the PurchaseHistory
        PurchaseHistoryDTO purchaseHistoryDTO = purchaseHistoryMapper.toDto(purchaseHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, purchaseHistoryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(purchaseHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PurchaseHistory in the database
        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findAll().collectList().block();
        assertThat(purchaseHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PurchaseHistory in Elasticsearch
        verify(mockPurchaseHistorySearchRepository, times(0)).save(purchaseHistory);
    }

    @Test
    void patchWithIdMismatchPurchaseHistory() throws Exception {
        int databaseSizeBeforeUpdate = purchaseHistoryRepository.findAll().collectList().block().size();
        purchaseHistory.setId(count.incrementAndGet());

        // Create the PurchaseHistory
        PurchaseHistoryDTO purchaseHistoryDTO = purchaseHistoryMapper.toDto(purchaseHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(purchaseHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PurchaseHistory in the database
        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findAll().collectList().block();
        assertThat(purchaseHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PurchaseHistory in Elasticsearch
        verify(mockPurchaseHistorySearchRepository, times(0)).save(purchaseHistory);
    }

    @Test
    void patchWithMissingIdPathParamPurchaseHistory() throws Exception {
        int databaseSizeBeforeUpdate = purchaseHistoryRepository.findAll().collectList().block().size();
        purchaseHistory.setId(count.incrementAndGet());

        // Create the PurchaseHistory
        PurchaseHistoryDTO purchaseHistoryDTO = purchaseHistoryMapper.toDto(purchaseHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(purchaseHistoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PurchaseHistory in the database
        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findAll().collectList().block();
        assertThat(purchaseHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PurchaseHistory in Elasticsearch
        verify(mockPurchaseHistorySearchRepository, times(0)).save(purchaseHistory);
    }

    @Test
    void deletePurchaseHistory() {
        // Configure the mock search repository
        when(mockPurchaseHistorySearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockPurchaseHistorySearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        purchaseHistoryRepository.save(purchaseHistory).block();

        int databaseSizeBeforeDelete = purchaseHistoryRepository.findAll().collectList().block().size();

        // Delete the purchaseHistory
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, purchaseHistory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findAll().collectList().block();
        assertThat(purchaseHistoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PurchaseHistory in Elasticsearch
        verify(mockPurchaseHistorySearchRepository, times(1)).deleteById(purchaseHistory.getId());
    }

    @Test
    void searchPurchaseHistory() {
        // Configure the mock search repository
        when(mockPurchaseHistorySearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        purchaseHistoryRepository.save(purchaseHistory).block();
        when(mockPurchaseHistorySearchRepository.search("id:" + purchaseHistory.getId())).thenReturn(Flux.just(purchaseHistory));

        // Search the purchaseHistory
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + purchaseHistory.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(purchaseHistory.getId().intValue()))
            .jsonPath("$.[*].purchaseDate")
            .value(hasItem(DEFAULT_PURCHASE_DATE.toString()))
            .jsonPath("$.[*].shippingDate")
            .value(hasItem(DEFAULT_SHIPPING_DATE.toString()))
            .jsonPath("$.[*].billingAddress")
            .value(hasItem(DEFAULT_BILLING_ADDRESS))
            .jsonPath("$.[*].paymentStatus")
            .value(hasItem(DEFAULT_PAYMENT_STATUS.toString()));
    }
}
