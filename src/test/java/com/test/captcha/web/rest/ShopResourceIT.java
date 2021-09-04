package com.test.captcha.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.test.captcha.IntegrationTest;
import com.test.captcha.domain.Shop;
import com.test.captcha.repository.ShopRepository;
import com.test.captcha.repository.search.ShopSearchRepository;
import com.test.captcha.service.EntityManager;
import com.test.captcha.service.dto.ShopDTO;
import com.test.captcha.service.mapper.ShopMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link ShopResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class ShopResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/shops";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/shops";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopMapper shopMapper;

    /**
     * This repository is mocked in the com.test.captcha.repository.search test package.
     *
     * @see com.test.captcha.repository.search.ShopSearchRepositoryMockConfiguration
     */
    @Autowired
    private ShopSearchRepository mockShopSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Shop shop;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shop createEntity(EntityManager em) {
        Shop shop = new Shop().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).createdDate(DEFAULT_CREATED_DATE);
        return shop;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shop createUpdatedEntity(EntityManager em) {
        Shop shop = new Shop().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).createdDate(UPDATED_CREATED_DATE);
        return shop;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Shop.class).block();
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
        shop = createEntity(em);
    }

    @Test
    void createShop() throws Exception {
        int databaseSizeBeforeCreate = shopRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockShopSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll().collectList().block();
        assertThat(shopList).hasSize(databaseSizeBeforeCreate + 1);
        Shop testShop = shopList.get(shopList.size() - 1);
        assertThat(testShop.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testShop.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testShop.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);

        // Validate the Shop in Elasticsearch
        verify(mockShopSearchRepository, times(1)).save(testShop);
    }

    @Test
    void createShopWithExistingId() throws Exception {
        // Create the Shop with an existing ID
        shop.setId(1L);
        ShopDTO shopDTO = shopMapper.toDto(shop);

        int databaseSizeBeforeCreate = shopRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll().collectList().block();
        assertThat(shopList).hasSize(databaseSizeBeforeCreate);

        // Validate the Shop in Elasticsearch
        verify(mockShopSearchRepository, times(0)).save(shop);
    }

    @Test
    void getAllShops() {
        // Initialize the database
        shopRepository.save(shop).block();

        // Get all the shopList
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
            .value(hasItem(shop.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()));
    }

    @Test
    void getShop() {
        // Initialize the database
        shopRepository.save(shop).block();

        // Get the shop
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, shop.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(shop.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()));
    }

    @Test
    void getNonExistingShop() {
        // Get the shop
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewShop() throws Exception {
        // Configure the mock search repository
        when(mockShopSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        shopRepository.save(shop).block();

        int databaseSizeBeforeUpdate = shopRepository.findAll().collectList().block().size();

        // Update the shop
        Shop updatedShop = shopRepository.findById(shop.getId()).block();
        updatedShop.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).createdDate(UPDATED_CREATED_DATE);
        ShopDTO shopDTO = shopMapper.toDto(updatedShop);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shopDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll().collectList().block();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
        Shop testShop = shopList.get(shopList.size() - 1);
        assertThat(testShop.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testShop.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testShop.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);

        // Validate the Shop in Elasticsearch
        verify(mockShopSearchRepository).save(testShop);
    }

    @Test
    void putNonExistingShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().collectList().block().size();
        shop.setId(count.incrementAndGet());

        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shopDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll().collectList().block();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Shop in Elasticsearch
        verify(mockShopSearchRepository, times(0)).save(shop);
    }

    @Test
    void putWithIdMismatchShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().collectList().block().size();
        shop.setId(count.incrementAndGet());

        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll().collectList().block();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Shop in Elasticsearch
        verify(mockShopSearchRepository, times(0)).save(shop);
    }

    @Test
    void putWithMissingIdPathParamShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().collectList().block().size();
        shop.setId(count.incrementAndGet());

        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll().collectList().block();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Shop in Elasticsearch
        verify(mockShopSearchRepository, times(0)).save(shop);
    }

    @Test
    void partialUpdateShopWithPatch() throws Exception {
        // Initialize the database
        shopRepository.save(shop).block();

        int databaseSizeBeforeUpdate = shopRepository.findAll().collectList().block().size();

        // Update the shop using partial update
        Shop partialUpdatedShop = new Shop();
        partialUpdatedShop.setId(shop.getId());

        partialUpdatedShop.createdDate(UPDATED_CREATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShop.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedShop))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll().collectList().block();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
        Shop testShop = shopList.get(shopList.size() - 1);
        assertThat(testShop.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testShop.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testShop.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    void fullUpdateShopWithPatch() throws Exception {
        // Initialize the database
        shopRepository.save(shop).block();

        int databaseSizeBeforeUpdate = shopRepository.findAll().collectList().block().size();

        // Update the shop using partial update
        Shop partialUpdatedShop = new Shop();
        partialUpdatedShop.setId(shop.getId());

        partialUpdatedShop.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).createdDate(UPDATED_CREATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShop.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedShop))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll().collectList().block();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
        Shop testShop = shopList.get(shopList.size() - 1);
        assertThat(testShop.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testShop.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testShop.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    void patchNonExistingShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().collectList().block().size();
        shop.setId(count.incrementAndGet());

        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, shopDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll().collectList().block();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Shop in Elasticsearch
        verify(mockShopSearchRepository, times(0)).save(shop);
    }

    @Test
    void patchWithIdMismatchShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().collectList().block().size();
        shop.setId(count.incrementAndGet());

        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll().collectList().block();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Shop in Elasticsearch
        verify(mockShopSearchRepository, times(0)).save(shop);
    }

    @Test
    void patchWithMissingIdPathParamShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().collectList().block().size();
        shop.setId(count.incrementAndGet());

        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(shopDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll().collectList().block();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Shop in Elasticsearch
        verify(mockShopSearchRepository, times(0)).save(shop);
    }

    @Test
    void deleteShop() {
        // Configure the mock search repository
        when(mockShopSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockShopSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        shopRepository.save(shop).block();

        int databaseSizeBeforeDelete = shopRepository.findAll().collectList().block().size();

        // Delete the shop
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, shop.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Shop> shopList = shopRepository.findAll().collectList().block();
        assertThat(shopList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Shop in Elasticsearch
        verify(mockShopSearchRepository, times(1)).deleteById(shop.getId());
    }

    @Test
    void searchShop() {
        // Configure the mock search repository
        when(mockShopSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockShopSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        shopRepository.save(shop).block();
        when(mockShopSearchRepository.search("id:" + shop.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(shop));

        // Search the shop
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + shop.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(shop.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()));
    }
}
