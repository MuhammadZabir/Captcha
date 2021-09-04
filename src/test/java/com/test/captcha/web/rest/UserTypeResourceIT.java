package com.test.captcha.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.test.captcha.IntegrationTest;
import com.test.captcha.domain.UserType;
import com.test.captcha.repository.UserTypeRepository;
import com.test.captcha.repository.search.UserTypeSearchRepository;
import com.test.captcha.service.EntityManager;
import com.test.captcha.service.dto.UserTypeDTO;
import com.test.captcha.service.mapper.UserTypeMapper;
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
 * Integration tests for the {@link UserTypeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class UserTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/user-types";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private UserTypeMapper userTypeMapper;

    /**
     * This repository is mocked in the com.test.captcha.repository.search test package.
     *
     * @see com.test.captcha.repository.search.UserTypeSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserTypeSearchRepository mockUserTypeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserType userType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserType createEntity(EntityManager em) {
        UserType userType = new UserType().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return userType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserType createUpdatedEntity(EntityManager em) {
        UserType userType = new UserType().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return userType;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UserType.class).block();
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
        userType = createEntity(em);
    }

    @Test
    void createUserType() throws Exception {
        int databaseSizeBeforeCreate = userTypeRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockUserTypeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the UserType
        UserTypeDTO userTypeDTO = userTypeMapper.toDto(userType);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userTypeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the UserType in the database
        List<UserType> userTypeList = userTypeRepository.findAll().collectList().block();
        assertThat(userTypeList).hasSize(databaseSizeBeforeCreate + 1);
        UserType testUserType = userTypeList.get(userTypeList.size() - 1);
        assertThat(testUserType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUserType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the UserType in Elasticsearch
        verify(mockUserTypeSearchRepository, times(1)).save(testUserType);
    }

    @Test
    void createUserTypeWithExistingId() throws Exception {
        // Create the UserType with an existing ID
        userType.setId(1L);
        UserTypeDTO userTypeDTO = userTypeMapper.toDto(userType);

        int databaseSizeBeforeCreate = userTypeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserType in the database
        List<UserType> userTypeList = userTypeRepository.findAll().collectList().block();
        assertThat(userTypeList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserType in Elasticsearch
        verify(mockUserTypeSearchRepository, times(0)).save(userType);
    }

    @Test
    void getAllUserTypesAsStream() {
        // Initialize the database
        userTypeRepository.save(userType).block();

        List<UserType> userTypeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(UserTypeDTO.class)
            .getResponseBody()
            .map(userTypeMapper::toEntity)
            .filter(userType::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(userTypeList).isNotNull();
        assertThat(userTypeList).hasSize(1);
        UserType testUserType = userTypeList.get(0);
        assertThat(testUserType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUserType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllUserTypes() {
        // Initialize the database
        userTypeRepository.save(userType).block();

        // Get all the userTypeList
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
            .value(hasItem(userType.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getUserType() {
        // Initialize the database
        userTypeRepository.save(userType).block();

        // Get the userType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userType.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingUserType() {
        // Get the userType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewUserType() throws Exception {
        // Configure the mock search repository
        when(mockUserTypeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        userTypeRepository.save(userType).block();

        int databaseSizeBeforeUpdate = userTypeRepository.findAll().collectList().block().size();

        // Update the userType
        UserType updatedUserType = userTypeRepository.findById(userType.getId()).block();
        updatedUserType.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        UserTypeDTO userTypeDTO = userTypeMapper.toDto(updatedUserType);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userTypeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userTypeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserType in the database
        List<UserType> userTypeList = userTypeRepository.findAll().collectList().block();
        assertThat(userTypeList).hasSize(databaseSizeBeforeUpdate);
        UserType testUserType = userTypeList.get(userTypeList.size() - 1);
        assertThat(testUserType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the UserType in Elasticsearch
        verify(mockUserTypeSearchRepository).save(testUserType);
    }

    @Test
    void putNonExistingUserType() throws Exception {
        int databaseSizeBeforeUpdate = userTypeRepository.findAll().collectList().block().size();
        userType.setId(count.incrementAndGet());

        // Create the UserType
        UserTypeDTO userTypeDTO = userTypeMapper.toDto(userType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userTypeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserType in the database
        List<UserType> userTypeList = userTypeRepository.findAll().collectList().block();
        assertThat(userTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserType in Elasticsearch
        verify(mockUserTypeSearchRepository, times(0)).save(userType);
    }

    @Test
    void putWithIdMismatchUserType() throws Exception {
        int databaseSizeBeforeUpdate = userTypeRepository.findAll().collectList().block().size();
        userType.setId(count.incrementAndGet());

        // Create the UserType
        UserTypeDTO userTypeDTO = userTypeMapper.toDto(userType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserType in the database
        List<UserType> userTypeList = userTypeRepository.findAll().collectList().block();
        assertThat(userTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserType in Elasticsearch
        verify(mockUserTypeSearchRepository, times(0)).save(userType);
    }

    @Test
    void putWithMissingIdPathParamUserType() throws Exception {
        int databaseSizeBeforeUpdate = userTypeRepository.findAll().collectList().block().size();
        userType.setId(count.incrementAndGet());

        // Create the UserType
        UserTypeDTO userTypeDTO = userTypeMapper.toDto(userType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userTypeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserType in the database
        List<UserType> userTypeList = userTypeRepository.findAll().collectList().block();
        assertThat(userTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserType in Elasticsearch
        verify(mockUserTypeSearchRepository, times(0)).save(userType);
    }

    @Test
    void partialUpdateUserTypeWithPatch() throws Exception {
        // Initialize the database
        userTypeRepository.save(userType).block();

        int databaseSizeBeforeUpdate = userTypeRepository.findAll().collectList().block().size();

        // Update the userType using partial update
        UserType partialUpdatedUserType = new UserType();
        partialUpdatedUserType.setId(userType.getId());

        partialUpdatedUserType.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserType in the database
        List<UserType> userTypeList = userTypeRepository.findAll().collectList().block();
        assertThat(userTypeList).hasSize(databaseSizeBeforeUpdate);
        UserType testUserType = userTypeList.get(userTypeList.size() - 1);
        assertThat(testUserType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdateUserTypeWithPatch() throws Exception {
        // Initialize the database
        userTypeRepository.save(userType).block();

        int databaseSizeBeforeUpdate = userTypeRepository.findAll().collectList().block().size();

        // Update the userType using partial update
        UserType partialUpdatedUserType = new UserType();
        partialUpdatedUserType.setId(userType.getId());

        partialUpdatedUserType.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserType in the database
        List<UserType> userTypeList = userTypeRepository.findAll().collectList().block();
        assertThat(userTypeList).hasSize(databaseSizeBeforeUpdate);
        UserType testUserType = userTypeList.get(userTypeList.size() - 1);
        assertThat(testUserType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingUserType() throws Exception {
        int databaseSizeBeforeUpdate = userTypeRepository.findAll().collectList().block().size();
        userType.setId(count.incrementAndGet());

        // Create the UserType
        UserTypeDTO userTypeDTO = userTypeMapper.toDto(userType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userTypeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserType in the database
        List<UserType> userTypeList = userTypeRepository.findAll().collectList().block();
        assertThat(userTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserType in Elasticsearch
        verify(mockUserTypeSearchRepository, times(0)).save(userType);
    }

    @Test
    void patchWithIdMismatchUserType() throws Exception {
        int databaseSizeBeforeUpdate = userTypeRepository.findAll().collectList().block().size();
        userType.setId(count.incrementAndGet());

        // Create the UserType
        UserTypeDTO userTypeDTO = userTypeMapper.toDto(userType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserType in the database
        List<UserType> userTypeList = userTypeRepository.findAll().collectList().block();
        assertThat(userTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserType in Elasticsearch
        verify(mockUserTypeSearchRepository, times(0)).save(userType);
    }

    @Test
    void patchWithMissingIdPathParamUserType() throws Exception {
        int databaseSizeBeforeUpdate = userTypeRepository.findAll().collectList().block().size();
        userType.setId(count.incrementAndGet());

        // Create the UserType
        UserTypeDTO userTypeDTO = userTypeMapper.toDto(userType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userTypeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserType in the database
        List<UserType> userTypeList = userTypeRepository.findAll().collectList().block();
        assertThat(userTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserType in Elasticsearch
        verify(mockUserTypeSearchRepository, times(0)).save(userType);
    }

    @Test
    void deleteUserType() {
        // Configure the mock search repository
        when(mockUserTypeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockUserTypeSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        userTypeRepository.save(userType).block();

        int databaseSizeBeforeDelete = userTypeRepository.findAll().collectList().block().size();

        // Delete the userType
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<UserType> userTypeList = userTypeRepository.findAll().collectList().block();
        assertThat(userTypeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserType in Elasticsearch
        verify(mockUserTypeSearchRepository, times(1)).deleteById(userType.getId());
    }

    @Test
    void searchUserType() {
        // Configure the mock search repository
        when(mockUserTypeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        userTypeRepository.save(userType).block();
        when(mockUserTypeSearchRepository.search("id:" + userType.getId())).thenReturn(Flux.just(userType));

        // Search the userType
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + userType.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(userType.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }
}
