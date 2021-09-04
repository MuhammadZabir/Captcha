package com.test.captcha.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.test.captcha.IntegrationTest;
import com.test.captcha.domain.ShopReview;
import com.test.captcha.repository.ShopReviewRepository;
import com.test.captcha.repository.search.ShopReviewSearchRepository;
import com.test.captcha.service.EntityManager;
import com.test.captcha.service.dto.ShopReviewDTO;
import com.test.captcha.service.mapper.ShopReviewMapper;
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
 * Integration tests for the {@link ShopReviewResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class ShopReviewResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;

    private static final LocalDate DEFAULT_REVIEW_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_REVIEW_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/shop-reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/shop-reviews";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ShopReviewRepository shopReviewRepository;

    @Autowired
    private ShopReviewMapper shopReviewMapper;

    /**
     * This repository is mocked in the com.test.captcha.repository.search test package.
     *
     * @see com.test.captcha.repository.search.ShopReviewSearchRepositoryMockConfiguration
     */
    @Autowired
    private ShopReviewSearchRepository mockShopReviewSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ShopReview shopReview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShopReview createEntity(EntityManager em) {
        ShopReview shopReview = new ShopReview().description(DEFAULT_DESCRIPTION).rating(DEFAULT_RATING).reviewDate(DEFAULT_REVIEW_DATE);
        return shopReview;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShopReview createUpdatedEntity(EntityManager em) {
        ShopReview shopReview = new ShopReview().description(UPDATED_DESCRIPTION).rating(UPDATED_RATING).reviewDate(UPDATED_REVIEW_DATE);
        return shopReview;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ShopReview.class).block();
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
        shopReview = createEntity(em);
    }

    @Test
    void createShopReview() throws Exception {
        int databaseSizeBeforeCreate = shopReviewRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockShopReviewSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the ShopReview
        ShopReviewDTO shopReviewDTO = shopReviewMapper.toDto(shopReview);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopReviewDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ShopReview in the database
        List<ShopReview> shopReviewList = shopReviewRepository.findAll().collectList().block();
        assertThat(shopReviewList).hasSize(databaseSizeBeforeCreate + 1);
        ShopReview testShopReview = shopReviewList.get(shopReviewList.size() - 1);
        assertThat(testShopReview.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testShopReview.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testShopReview.getReviewDate()).isEqualTo(DEFAULT_REVIEW_DATE);

        // Validate the ShopReview in Elasticsearch
        verify(mockShopReviewSearchRepository, times(1)).save(testShopReview);
    }

    @Test
    void createShopReviewWithExistingId() throws Exception {
        // Create the ShopReview with an existing ID
        shopReview.setId(1L);
        ShopReviewDTO shopReviewDTO = shopReviewMapper.toDto(shopReview);

        int databaseSizeBeforeCreate = shopReviewRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShopReview in the database
        List<ShopReview> shopReviewList = shopReviewRepository.findAll().collectList().block();
        assertThat(shopReviewList).hasSize(databaseSizeBeforeCreate);

        // Validate the ShopReview in Elasticsearch
        verify(mockShopReviewSearchRepository, times(0)).save(shopReview);
    }

    @Test
    void getAllShopReviewsAsStream() {
        // Initialize the database
        shopReviewRepository.save(shopReview).block();

        List<ShopReview> shopReviewList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ShopReviewDTO.class)
            .getResponseBody()
            .map(shopReviewMapper::toEntity)
            .filter(shopReview::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(shopReviewList).isNotNull();
        assertThat(shopReviewList).hasSize(1);
        ShopReview testShopReview = shopReviewList.get(0);
        assertThat(testShopReview.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testShopReview.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testShopReview.getReviewDate()).isEqualTo(DEFAULT_REVIEW_DATE);
    }

    @Test
    void getAllShopReviews() {
        // Initialize the database
        shopReviewRepository.save(shopReview).block();

        // Get all the shopReviewList
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
            .value(hasItem(shopReview.getId().intValue()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].reviewDate")
            .value(hasItem(DEFAULT_REVIEW_DATE.toString()));
    }

    @Test
    void getShopReview() {
        // Initialize the database
        shopReviewRepository.save(shopReview).block();

        // Get the shopReview
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, shopReview.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(shopReview.getId().intValue()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING))
            .jsonPath("$.reviewDate")
            .value(is(DEFAULT_REVIEW_DATE.toString()));
    }

    @Test
    void getNonExistingShopReview() {
        // Get the shopReview
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewShopReview() throws Exception {
        // Configure the mock search repository
        when(mockShopReviewSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        shopReviewRepository.save(shopReview).block();

        int databaseSizeBeforeUpdate = shopReviewRepository.findAll().collectList().block().size();

        // Update the shopReview
        ShopReview updatedShopReview = shopReviewRepository.findById(shopReview.getId()).block();
        updatedShopReview.description(UPDATED_DESCRIPTION).rating(UPDATED_RATING).reviewDate(UPDATED_REVIEW_DATE);
        ShopReviewDTO shopReviewDTO = shopReviewMapper.toDto(updatedShopReview);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shopReviewDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopReviewDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ShopReview in the database
        List<ShopReview> shopReviewList = shopReviewRepository.findAll().collectList().block();
        assertThat(shopReviewList).hasSize(databaseSizeBeforeUpdate);
        ShopReview testShopReview = shopReviewList.get(shopReviewList.size() - 1);
        assertThat(testShopReview.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testShopReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testShopReview.getReviewDate()).isEqualTo(UPDATED_REVIEW_DATE);

        // Validate the ShopReview in Elasticsearch
        verify(mockShopReviewSearchRepository).save(testShopReview);
    }

    @Test
    void putNonExistingShopReview() throws Exception {
        int databaseSizeBeforeUpdate = shopReviewRepository.findAll().collectList().block().size();
        shopReview.setId(count.incrementAndGet());

        // Create the ShopReview
        ShopReviewDTO shopReviewDTO = shopReviewMapper.toDto(shopReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shopReviewDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShopReview in the database
        List<ShopReview> shopReviewList = shopReviewRepository.findAll().collectList().block();
        assertThat(shopReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ShopReview in Elasticsearch
        verify(mockShopReviewSearchRepository, times(0)).save(shopReview);
    }

    @Test
    void putWithIdMismatchShopReview() throws Exception {
        int databaseSizeBeforeUpdate = shopReviewRepository.findAll().collectList().block().size();
        shopReview.setId(count.incrementAndGet());

        // Create the ShopReview
        ShopReviewDTO shopReviewDTO = shopReviewMapper.toDto(shopReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShopReview in the database
        List<ShopReview> shopReviewList = shopReviewRepository.findAll().collectList().block();
        assertThat(shopReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ShopReview in Elasticsearch
        verify(mockShopReviewSearchRepository, times(0)).save(shopReview);
    }

    @Test
    void putWithMissingIdPathParamShopReview() throws Exception {
        int databaseSizeBeforeUpdate = shopReviewRepository.findAll().collectList().block().size();
        shopReview.setId(count.incrementAndGet());

        // Create the ShopReview
        ShopReviewDTO shopReviewDTO = shopReviewMapper.toDto(shopReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopReviewDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ShopReview in the database
        List<ShopReview> shopReviewList = shopReviewRepository.findAll().collectList().block();
        assertThat(shopReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ShopReview in Elasticsearch
        verify(mockShopReviewSearchRepository, times(0)).save(shopReview);
    }

    @Test
    void partialUpdateShopReviewWithPatch() throws Exception {
        // Initialize the database
        shopReviewRepository.save(shopReview).block();

        int databaseSizeBeforeUpdate = shopReviewRepository.findAll().collectList().block().size();

        // Update the shopReview using partial update
        ShopReview partialUpdatedShopReview = new ShopReview();
        partialUpdatedShopReview.setId(shopReview.getId());

        partialUpdatedShopReview.rating(UPDATED_RATING);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShopReview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedShopReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ShopReview in the database
        List<ShopReview> shopReviewList = shopReviewRepository.findAll().collectList().block();
        assertThat(shopReviewList).hasSize(databaseSizeBeforeUpdate);
        ShopReview testShopReview = shopReviewList.get(shopReviewList.size() - 1);
        assertThat(testShopReview.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testShopReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testShopReview.getReviewDate()).isEqualTo(DEFAULT_REVIEW_DATE);
    }

    @Test
    void fullUpdateShopReviewWithPatch() throws Exception {
        // Initialize the database
        shopReviewRepository.save(shopReview).block();

        int databaseSizeBeforeUpdate = shopReviewRepository.findAll().collectList().block().size();

        // Update the shopReview using partial update
        ShopReview partialUpdatedShopReview = new ShopReview();
        partialUpdatedShopReview.setId(shopReview.getId());

        partialUpdatedShopReview.description(UPDATED_DESCRIPTION).rating(UPDATED_RATING).reviewDate(UPDATED_REVIEW_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShopReview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedShopReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ShopReview in the database
        List<ShopReview> shopReviewList = shopReviewRepository.findAll().collectList().block();
        assertThat(shopReviewList).hasSize(databaseSizeBeforeUpdate);
        ShopReview testShopReview = shopReviewList.get(shopReviewList.size() - 1);
        assertThat(testShopReview.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testShopReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testShopReview.getReviewDate()).isEqualTo(UPDATED_REVIEW_DATE);
    }

    @Test
    void patchNonExistingShopReview() throws Exception {
        int databaseSizeBeforeUpdate = shopReviewRepository.findAll().collectList().block().size();
        shopReview.setId(count.incrementAndGet());

        // Create the ShopReview
        ShopReviewDTO shopReviewDTO = shopReviewMapper.toDto(shopReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, shopReviewDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShopReview in the database
        List<ShopReview> shopReviewList = shopReviewRepository.findAll().collectList().block();
        assertThat(shopReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ShopReview in Elasticsearch
        verify(mockShopReviewSearchRepository, times(0)).save(shopReview);
    }

    @Test
    void patchWithIdMismatchShopReview() throws Exception {
        int databaseSizeBeforeUpdate = shopReviewRepository.findAll().collectList().block().size();
        shopReview.setId(count.incrementAndGet());

        // Create the ShopReview
        ShopReviewDTO shopReviewDTO = shopReviewMapper.toDto(shopReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShopReview in the database
        List<ShopReview> shopReviewList = shopReviewRepository.findAll().collectList().block();
        assertThat(shopReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ShopReview in Elasticsearch
        verify(mockShopReviewSearchRepository, times(0)).save(shopReview);
    }

    @Test
    void patchWithMissingIdPathParamShopReview() throws Exception {
        int databaseSizeBeforeUpdate = shopReviewRepository.findAll().collectList().block().size();
        shopReview.setId(count.incrementAndGet());

        // Create the ShopReview
        ShopReviewDTO shopReviewDTO = shopReviewMapper.toDto(shopReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopReviewDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ShopReview in the database
        List<ShopReview> shopReviewList = shopReviewRepository.findAll().collectList().block();
        assertThat(shopReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ShopReview in Elasticsearch
        verify(mockShopReviewSearchRepository, times(0)).save(shopReview);
    }

    @Test
    void deleteShopReview() {
        // Configure the mock search repository
        when(mockShopReviewSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockShopReviewSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        shopReviewRepository.save(shopReview).block();

        int databaseSizeBeforeDelete = shopReviewRepository.findAll().collectList().block().size();

        // Delete the shopReview
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, shopReview.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ShopReview> shopReviewList = shopReviewRepository.findAll().collectList().block();
        assertThat(shopReviewList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ShopReview in Elasticsearch
        verify(mockShopReviewSearchRepository, times(1)).deleteById(shopReview.getId());
    }

    @Test
    void searchShopReview() {
        // Configure the mock search repository
        when(mockShopReviewSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        shopReviewRepository.save(shopReview).block();
        when(mockShopReviewSearchRepository.search("id:" + shopReview.getId())).thenReturn(Flux.just(shopReview));

        // Search the shopReview
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + shopReview.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(shopReview.getId().intValue()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].reviewDate")
            .value(hasItem(DEFAULT_REVIEW_DATE.toString()));
    }
}
