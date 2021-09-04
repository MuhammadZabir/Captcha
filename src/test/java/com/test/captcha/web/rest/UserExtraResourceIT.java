package com.test.captcha.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.test.captcha.IntegrationTest;
import com.test.captcha.domain.UserExtra;
import com.test.captcha.repository.UserExtraRepository;
import com.test.captcha.repository.search.UserExtraSearchRepository;
import com.test.captcha.service.EntityManager;
import com.test.captcha.service.dto.UserExtraDTO;
import com.test.captcha.service.mapper.UserExtraMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link UserExtraResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class UserExtraResourceIT {

    private static final String DEFAULT_BILLING_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_BILLING_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-extras";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/user-extras";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserExtraRepository userExtraRepository;

    @Autowired
    private UserExtraMapper userExtraMapper;

    /**
     * This repository is mocked in the com.test.captcha.repository.search test package.
     *
     * @see com.test.captcha.repository.search.UserExtraSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserExtraSearchRepository mockUserExtraSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserExtra userExtra;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserExtra createEntity(EntityManager em) {
        UserExtra userExtra = new UserExtra().billingAddress(DEFAULT_BILLING_ADDRESS);
        return userExtra;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserExtra createUpdatedEntity(EntityManager em) {
        UserExtra userExtra = new UserExtra().billingAddress(UPDATED_BILLING_ADDRESS);
        return userExtra;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UserExtra.class).block();
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
        userExtra = createEntity(em);
    }

    @Test
    void createUserExtra() throws Exception {
        int databaseSizeBeforeCreate = userExtraRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockUserExtraSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the UserExtra
        UserExtraDTO userExtraDTO = userExtraMapper.toDto(userExtra);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userExtraDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the UserExtra in the database
        List<UserExtra> userExtraList = userExtraRepository.findAll().collectList().block();
        assertThat(userExtraList).hasSize(databaseSizeBeforeCreate + 1);
        UserExtra testUserExtra = userExtraList.get(userExtraList.size() - 1);
        assertThat(testUserExtra.getBillingAddress()).isEqualTo(DEFAULT_BILLING_ADDRESS);

        // Validate the UserExtra in Elasticsearch
        verify(mockUserExtraSearchRepository, times(1)).save(testUserExtra);
    }

    @Test
    void createUserExtraWithExistingId() throws Exception {
        // Create the UserExtra with an existing ID
        userExtra.setId(1L);
        UserExtraDTO userExtraDTO = userExtraMapper.toDto(userExtra);

        int databaseSizeBeforeCreate = userExtraRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userExtraDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserExtra in the database
        List<UserExtra> userExtraList = userExtraRepository.findAll().collectList().block();
        assertThat(userExtraList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserExtra in Elasticsearch
        verify(mockUserExtraSearchRepository, times(0)).save(userExtra);
    }

    @Test
    void getAllUserExtras() {
        // Initialize the database
        userExtraRepository.save(userExtra).block();

        // Get all the userExtraList
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
            .value(hasItem(userExtra.getId().intValue()))
            .jsonPath("$.[*].billingAddress")
            .value(hasItem(DEFAULT_BILLING_ADDRESS));
    }

    @Test
    void getUserExtra() {
        // Initialize the database
        userExtraRepository.save(userExtra).block();

        // Get the userExtra
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userExtra.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userExtra.getId().intValue()))
            .jsonPath("$.billingAddress")
            .value(is(DEFAULT_BILLING_ADDRESS));
    }

    @Test
    void getNonExistingUserExtra() {
        // Get the userExtra
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewUserExtra() throws Exception {
        // Configure the mock search repository
        when(mockUserExtraSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        userExtraRepository.save(userExtra).block();

        int databaseSizeBeforeUpdate = userExtraRepository.findAll().collectList().block().size();

        // Update the userExtra
        UserExtra updatedUserExtra = userExtraRepository.findById(userExtra.getId()).block();
        updatedUserExtra.billingAddress(UPDATED_BILLING_ADDRESS);
        UserExtraDTO userExtraDTO = userExtraMapper.toDto(updatedUserExtra);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userExtraDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userExtraDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserExtra in the database
        List<UserExtra> userExtraList = userExtraRepository.findAll().collectList().block();
        assertThat(userExtraList).hasSize(databaseSizeBeforeUpdate);
        UserExtra testUserExtra = userExtraList.get(userExtraList.size() - 1);
        assertThat(testUserExtra.getBillingAddress()).isEqualTo(UPDATED_BILLING_ADDRESS);

        // Validate the UserExtra in Elasticsearch
        verify(mockUserExtraSearchRepository).save(testUserExtra);
    }

    @Test
    void putNonExistingUserExtra() throws Exception {
        int databaseSizeBeforeUpdate = userExtraRepository.findAll().collectList().block().size();
        userExtra.setId(count.incrementAndGet());

        // Create the UserExtra
        UserExtraDTO userExtraDTO = userExtraMapper.toDto(userExtra);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userExtraDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userExtraDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserExtra in the database
        List<UserExtra> userExtraList = userExtraRepository.findAll().collectList().block();
        assertThat(userExtraList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserExtra in Elasticsearch
        verify(mockUserExtraSearchRepository, times(0)).save(userExtra);
    }

    @Test
    void putWithIdMismatchUserExtra() throws Exception {
        int databaseSizeBeforeUpdate = userExtraRepository.findAll().collectList().block().size();
        userExtra.setId(count.incrementAndGet());

        // Create the UserExtra
        UserExtraDTO userExtraDTO = userExtraMapper.toDto(userExtra);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userExtraDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserExtra in the database
        List<UserExtra> userExtraList = userExtraRepository.findAll().collectList().block();
        assertThat(userExtraList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserExtra in Elasticsearch
        verify(mockUserExtraSearchRepository, times(0)).save(userExtra);
    }

    @Test
    void putWithMissingIdPathParamUserExtra() throws Exception {
        int databaseSizeBeforeUpdate = userExtraRepository.findAll().collectList().block().size();
        userExtra.setId(count.incrementAndGet());

        // Create the UserExtra
        UserExtraDTO userExtraDTO = userExtraMapper.toDto(userExtra);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userExtraDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserExtra in the database
        List<UserExtra> userExtraList = userExtraRepository.findAll().collectList().block();
        assertThat(userExtraList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserExtra in Elasticsearch
        verify(mockUserExtraSearchRepository, times(0)).save(userExtra);
    }

    @Test
    void partialUpdateUserExtraWithPatch() throws Exception {
        // Initialize the database
        userExtraRepository.save(userExtra).block();

        int databaseSizeBeforeUpdate = userExtraRepository.findAll().collectList().block().size();

        // Update the userExtra using partial update
        UserExtra partialUpdatedUserExtra = new UserExtra();
        partialUpdatedUserExtra.setId(userExtra.getId());

        partialUpdatedUserExtra.billingAddress(UPDATED_BILLING_ADDRESS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserExtra.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserExtra))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserExtra in the database
        List<UserExtra> userExtraList = userExtraRepository.findAll().collectList().block();
        assertThat(userExtraList).hasSize(databaseSizeBeforeUpdate);
        UserExtra testUserExtra = userExtraList.get(userExtraList.size() - 1);
        assertThat(testUserExtra.getBillingAddress()).isEqualTo(UPDATED_BILLING_ADDRESS);
    }

    @Test
    void fullUpdateUserExtraWithPatch() throws Exception {
        // Initialize the database
        userExtraRepository.save(userExtra).block();

        int databaseSizeBeforeUpdate = userExtraRepository.findAll().collectList().block().size();

        // Update the userExtra using partial update
        UserExtra partialUpdatedUserExtra = new UserExtra();
        partialUpdatedUserExtra.setId(userExtra.getId());

        partialUpdatedUserExtra.billingAddress(UPDATED_BILLING_ADDRESS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserExtra.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserExtra))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserExtra in the database
        List<UserExtra> userExtraList = userExtraRepository.findAll().collectList().block();
        assertThat(userExtraList).hasSize(databaseSizeBeforeUpdate);
        UserExtra testUserExtra = userExtraList.get(userExtraList.size() - 1);
        assertThat(testUserExtra.getBillingAddress()).isEqualTo(UPDATED_BILLING_ADDRESS);
    }

    @Test
    void patchNonExistingUserExtra() throws Exception {
        int databaseSizeBeforeUpdate = userExtraRepository.findAll().collectList().block().size();
        userExtra.setId(count.incrementAndGet());

        // Create the UserExtra
        UserExtraDTO userExtraDTO = userExtraMapper.toDto(userExtra);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userExtraDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userExtraDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserExtra in the database
        List<UserExtra> userExtraList = userExtraRepository.findAll().collectList().block();
        assertThat(userExtraList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserExtra in Elasticsearch
        verify(mockUserExtraSearchRepository, times(0)).save(userExtra);
    }

    @Test
    void patchWithIdMismatchUserExtra() throws Exception {
        int databaseSizeBeforeUpdate = userExtraRepository.findAll().collectList().block().size();
        userExtra.setId(count.incrementAndGet());

        // Create the UserExtra
        UserExtraDTO userExtraDTO = userExtraMapper.toDto(userExtra);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userExtraDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserExtra in the database
        List<UserExtra> userExtraList = userExtraRepository.findAll().collectList().block();
        assertThat(userExtraList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserExtra in Elasticsearch
        verify(mockUserExtraSearchRepository, times(0)).save(userExtra);
    }

    @Test
    void patchWithMissingIdPathParamUserExtra() throws Exception {
        int databaseSizeBeforeUpdate = userExtraRepository.findAll().collectList().block().size();
        userExtra.setId(count.incrementAndGet());

        // Create the UserExtra
        UserExtraDTO userExtraDTO = userExtraMapper.toDto(userExtra);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userExtraDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserExtra in the database
        List<UserExtra> userExtraList = userExtraRepository.findAll().collectList().block();
        assertThat(userExtraList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserExtra in Elasticsearch
        verify(mockUserExtraSearchRepository, times(0)).save(userExtra);
    }

    @Test
    void deleteUserExtra() {
        // Configure the mock search repository
        when(mockUserExtraSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockUserExtraSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        userExtraRepository.save(userExtra).block();

        int databaseSizeBeforeDelete = userExtraRepository.findAll().collectList().block().size();

        // Delete the userExtra
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userExtra.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<UserExtra> userExtraList = userExtraRepository.findAll().collectList().block();
        assertThat(userExtraList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserExtra in Elasticsearch
        verify(mockUserExtraSearchRepository, times(1)).deleteById(userExtra.getId());
    }

    @Test
    void searchUserExtra() {
        // Configure the mock search repository
        when(mockUserExtraSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockUserExtraSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        userExtraRepository.save(userExtra).block();
        when(mockUserExtraSearchRepository.search("id:" + userExtra.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(userExtra));

        // Search the userExtra
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + userExtra.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(userExtra.getId().intValue()))
            .jsonPath("$.[*].billingAddress")
            .value(hasItem(DEFAULT_BILLING_ADDRESS));
    }
}
