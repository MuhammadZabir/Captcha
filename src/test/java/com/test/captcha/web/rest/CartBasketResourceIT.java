package com.test.captcha.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.test.captcha.IntegrationTest;
import com.test.captcha.domain.CartBasket;
import com.test.captcha.repository.CartBasketRepository;
import com.test.captcha.repository.search.CartBasketSearchRepository;
import com.test.captcha.service.EntityManager;
import com.test.captcha.service.dto.CartBasketDTO;
import com.test.captcha.service.mapper.CartBasketMapper;
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
 * Integration tests for the {@link CartBasketResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class CartBasketResourceIT {

    private static final Integer DEFAULT_AMOUNT = 1;
    private static final Integer UPDATED_AMOUNT = 2;

    private static final String ENTITY_API_URL = "/api/cart-baskets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/cart-baskets";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CartBasketRepository cartBasketRepository;

    @Autowired
    private CartBasketMapper cartBasketMapper;

    /**
     * This repository is mocked in the com.test.captcha.repository.search test package.
     *
     * @see com.test.captcha.repository.search.CartBasketSearchRepositoryMockConfiguration
     */
    @Autowired
    private CartBasketSearchRepository mockCartBasketSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CartBasket cartBasket;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartBasket createEntity(EntityManager em) {
        CartBasket cartBasket = new CartBasket().amount(DEFAULT_AMOUNT);
        return cartBasket;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartBasket createUpdatedEntity(EntityManager em) {
        CartBasket cartBasket = new CartBasket().amount(UPDATED_AMOUNT);
        return cartBasket;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CartBasket.class).block();
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
        cartBasket = createEntity(em);
    }

    @Test
    void createCartBasket() throws Exception {
        int databaseSizeBeforeCreate = cartBasketRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockCartBasketSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the CartBasket
        CartBasketDTO cartBasketDTO = cartBasketMapper.toDto(cartBasket);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartBasketDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CartBasket in the database
        List<CartBasket> cartBasketList = cartBasketRepository.findAll().collectList().block();
        assertThat(cartBasketList).hasSize(databaseSizeBeforeCreate + 1);
        CartBasket testCartBasket = cartBasketList.get(cartBasketList.size() - 1);
        assertThat(testCartBasket.getAmount()).isEqualTo(DEFAULT_AMOUNT);

        // Validate the CartBasket in Elasticsearch
        verify(mockCartBasketSearchRepository, times(1)).save(testCartBasket);
    }

    @Test
    void createCartBasketWithExistingId() throws Exception {
        // Create the CartBasket with an existing ID
        cartBasket.setId(1L);
        CartBasketDTO cartBasketDTO = cartBasketMapper.toDto(cartBasket);

        int databaseSizeBeforeCreate = cartBasketRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartBasketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CartBasket in the database
        List<CartBasket> cartBasketList = cartBasketRepository.findAll().collectList().block();
        assertThat(cartBasketList).hasSize(databaseSizeBeforeCreate);

        // Validate the CartBasket in Elasticsearch
        verify(mockCartBasketSearchRepository, times(0)).save(cartBasket);
    }

    @Test
    void getAllCartBasketsAsStream() {
        // Initialize the database
        cartBasketRepository.save(cartBasket).block();

        List<CartBasket> cartBasketList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CartBasketDTO.class)
            .getResponseBody()
            .map(cartBasketMapper::toEntity)
            .filter(cartBasket::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(cartBasketList).isNotNull();
        assertThat(cartBasketList).hasSize(1);
        CartBasket testCartBasket = cartBasketList.get(0);
        assertThat(testCartBasket.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    void getAllCartBaskets() {
        // Initialize the database
        cartBasketRepository.save(cartBasket).block();

        // Get all the cartBasketList
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
            .value(hasItem(cartBasket.getId().intValue()))
            .jsonPath("$.[*].amount")
            .value(hasItem(DEFAULT_AMOUNT));
    }

    @Test
    void getCartBasket() {
        // Initialize the database
        cartBasketRepository.save(cartBasket).block();

        // Get the cartBasket
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, cartBasket.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(cartBasket.getId().intValue()))
            .jsonPath("$.amount")
            .value(is(DEFAULT_AMOUNT));
    }

    @Test
    void getNonExistingCartBasket() {
        // Get the cartBasket
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCartBasket() throws Exception {
        // Configure the mock search repository
        when(mockCartBasketSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        cartBasketRepository.save(cartBasket).block();

        int databaseSizeBeforeUpdate = cartBasketRepository.findAll().collectList().block().size();

        // Update the cartBasket
        CartBasket updatedCartBasket = cartBasketRepository.findById(cartBasket.getId()).block();
        updatedCartBasket.amount(UPDATED_AMOUNT);
        CartBasketDTO cartBasketDTO = cartBasketMapper.toDto(updatedCartBasket);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cartBasketDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartBasketDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CartBasket in the database
        List<CartBasket> cartBasketList = cartBasketRepository.findAll().collectList().block();
        assertThat(cartBasketList).hasSize(databaseSizeBeforeUpdate);
        CartBasket testCartBasket = cartBasketList.get(cartBasketList.size() - 1);
        assertThat(testCartBasket.getAmount()).isEqualTo(UPDATED_AMOUNT);

        // Validate the CartBasket in Elasticsearch
        verify(mockCartBasketSearchRepository).save(testCartBasket);
    }

    @Test
    void putNonExistingCartBasket() throws Exception {
        int databaseSizeBeforeUpdate = cartBasketRepository.findAll().collectList().block().size();
        cartBasket.setId(count.incrementAndGet());

        // Create the CartBasket
        CartBasketDTO cartBasketDTO = cartBasketMapper.toDto(cartBasket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cartBasketDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartBasketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CartBasket in the database
        List<CartBasket> cartBasketList = cartBasketRepository.findAll().collectList().block();
        assertThat(cartBasketList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CartBasket in Elasticsearch
        verify(mockCartBasketSearchRepository, times(0)).save(cartBasket);
    }

    @Test
    void putWithIdMismatchCartBasket() throws Exception {
        int databaseSizeBeforeUpdate = cartBasketRepository.findAll().collectList().block().size();
        cartBasket.setId(count.incrementAndGet());

        // Create the CartBasket
        CartBasketDTO cartBasketDTO = cartBasketMapper.toDto(cartBasket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartBasketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CartBasket in the database
        List<CartBasket> cartBasketList = cartBasketRepository.findAll().collectList().block();
        assertThat(cartBasketList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CartBasket in Elasticsearch
        verify(mockCartBasketSearchRepository, times(0)).save(cartBasket);
    }

    @Test
    void putWithMissingIdPathParamCartBasket() throws Exception {
        int databaseSizeBeforeUpdate = cartBasketRepository.findAll().collectList().block().size();
        cartBasket.setId(count.incrementAndGet());

        // Create the CartBasket
        CartBasketDTO cartBasketDTO = cartBasketMapper.toDto(cartBasket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartBasketDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CartBasket in the database
        List<CartBasket> cartBasketList = cartBasketRepository.findAll().collectList().block();
        assertThat(cartBasketList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CartBasket in Elasticsearch
        verify(mockCartBasketSearchRepository, times(0)).save(cartBasket);
    }

    @Test
    void partialUpdateCartBasketWithPatch() throws Exception {
        // Initialize the database
        cartBasketRepository.save(cartBasket).block();

        int databaseSizeBeforeUpdate = cartBasketRepository.findAll().collectList().block().size();

        // Update the cartBasket using partial update
        CartBasket partialUpdatedCartBasket = new CartBasket();
        partialUpdatedCartBasket.setId(cartBasket.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCartBasket.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCartBasket))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CartBasket in the database
        List<CartBasket> cartBasketList = cartBasketRepository.findAll().collectList().block();
        assertThat(cartBasketList).hasSize(databaseSizeBeforeUpdate);
        CartBasket testCartBasket = cartBasketList.get(cartBasketList.size() - 1);
        assertThat(testCartBasket.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    void fullUpdateCartBasketWithPatch() throws Exception {
        // Initialize the database
        cartBasketRepository.save(cartBasket).block();

        int databaseSizeBeforeUpdate = cartBasketRepository.findAll().collectList().block().size();

        // Update the cartBasket using partial update
        CartBasket partialUpdatedCartBasket = new CartBasket();
        partialUpdatedCartBasket.setId(cartBasket.getId());

        partialUpdatedCartBasket.amount(UPDATED_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCartBasket.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCartBasket))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CartBasket in the database
        List<CartBasket> cartBasketList = cartBasketRepository.findAll().collectList().block();
        assertThat(cartBasketList).hasSize(databaseSizeBeforeUpdate);
        CartBasket testCartBasket = cartBasketList.get(cartBasketList.size() - 1);
        assertThat(testCartBasket.getAmount()).isEqualTo(UPDATED_AMOUNT);
    }

    @Test
    void patchNonExistingCartBasket() throws Exception {
        int databaseSizeBeforeUpdate = cartBasketRepository.findAll().collectList().block().size();
        cartBasket.setId(count.incrementAndGet());

        // Create the CartBasket
        CartBasketDTO cartBasketDTO = cartBasketMapper.toDto(cartBasket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cartBasketDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartBasketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CartBasket in the database
        List<CartBasket> cartBasketList = cartBasketRepository.findAll().collectList().block();
        assertThat(cartBasketList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CartBasket in Elasticsearch
        verify(mockCartBasketSearchRepository, times(0)).save(cartBasket);
    }

    @Test
    void patchWithIdMismatchCartBasket() throws Exception {
        int databaseSizeBeforeUpdate = cartBasketRepository.findAll().collectList().block().size();
        cartBasket.setId(count.incrementAndGet());

        // Create the CartBasket
        CartBasketDTO cartBasketDTO = cartBasketMapper.toDto(cartBasket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartBasketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CartBasket in the database
        List<CartBasket> cartBasketList = cartBasketRepository.findAll().collectList().block();
        assertThat(cartBasketList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CartBasket in Elasticsearch
        verify(mockCartBasketSearchRepository, times(0)).save(cartBasket);
    }

    @Test
    void patchWithMissingIdPathParamCartBasket() throws Exception {
        int databaseSizeBeforeUpdate = cartBasketRepository.findAll().collectList().block().size();
        cartBasket.setId(count.incrementAndGet());

        // Create the CartBasket
        CartBasketDTO cartBasketDTO = cartBasketMapper.toDto(cartBasket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartBasketDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CartBasket in the database
        List<CartBasket> cartBasketList = cartBasketRepository.findAll().collectList().block();
        assertThat(cartBasketList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CartBasket in Elasticsearch
        verify(mockCartBasketSearchRepository, times(0)).save(cartBasket);
    }

    @Test
    void deleteCartBasket() {
        // Configure the mock search repository
        when(mockCartBasketSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockCartBasketSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        cartBasketRepository.save(cartBasket).block();

        int databaseSizeBeforeDelete = cartBasketRepository.findAll().collectList().block().size();

        // Delete the cartBasket
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, cartBasket.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CartBasket> cartBasketList = cartBasketRepository.findAll().collectList().block();
        assertThat(cartBasketList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CartBasket in Elasticsearch
        verify(mockCartBasketSearchRepository, times(1)).deleteById(cartBasket.getId());
    }

    @Test
    void searchCartBasket() {
        // Configure the mock search repository
        when(mockCartBasketSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        cartBasketRepository.save(cartBasket).block();
        when(mockCartBasketSearchRepository.search("id:" + cartBasket.getId())).thenReturn(Flux.just(cartBasket));

        // Search the cartBasket
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + cartBasket.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(cartBasket.getId().intValue()))
            .jsonPath("$.[*].amount")
            .value(hasItem(DEFAULT_AMOUNT));
    }
}
