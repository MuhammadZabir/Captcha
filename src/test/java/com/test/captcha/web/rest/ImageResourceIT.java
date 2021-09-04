package com.test.captcha.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.test.captcha.IntegrationTest;
import com.test.captcha.domain.Image;
import com.test.captcha.repository.ImageRepository;
import com.test.captcha.repository.search.ImageSearchRepository;
import com.test.captcha.service.EntityManager;
import com.test.captcha.service.dto.ImageDTO;
import com.test.captcha.service.mapper.ImageMapper;
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
 * Integration tests for the {@link ImageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class ImageResourceIT {

    private static final String DEFAULT_IMAGE_DIR = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_DIR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/images";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/images";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageMapper imageMapper;

    /**
     * This repository is mocked in the com.test.captcha.repository.search test package.
     *
     * @see com.test.captcha.repository.search.ImageSearchRepositoryMockConfiguration
     */
    @Autowired
    private ImageSearchRepository mockImageSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Image image;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Image createEntity(EntityManager em) {
        Image image = new Image().imageDir(DEFAULT_IMAGE_DIR);
        return image;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Image createUpdatedEntity(EntityManager em) {
        Image image = new Image().imageDir(UPDATED_IMAGE_DIR);
        return image;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Image.class).block();
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
        image = createEntity(em);
    }

    @Test
    void createImage() throws Exception {
        int databaseSizeBeforeCreate = imageRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockImageSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeCreate + 1);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getImageDir()).isEqualTo(DEFAULT_IMAGE_DIR);

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(1)).save(testImage);
    }

    @Test
    void createImageWithExistingId() throws Exception {
        // Create the Image with an existing ID
        image.setId(1L);
        ImageDTO imageDTO = imageMapper.toDto(image);

        int databaseSizeBeforeCreate = imageRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeCreate);

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(0)).save(image);
    }

    @Test
    void getAllImagesAsStream() {
        // Initialize the database
        imageRepository.save(image).block();

        List<Image> imageList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ImageDTO.class)
            .getResponseBody()
            .map(imageMapper::toEntity)
            .filter(image::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(imageList).isNotNull();
        assertThat(imageList).hasSize(1);
        Image testImage = imageList.get(0);
        assertThat(testImage.getImageDir()).isEqualTo(DEFAULT_IMAGE_DIR);
    }

    @Test
    void getAllImages() {
        // Initialize the database
        imageRepository.save(image).block();

        // Get all the imageList
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
            .value(hasItem(image.getId().intValue()))
            .jsonPath("$.[*].imageDir")
            .value(hasItem(DEFAULT_IMAGE_DIR));
    }

    @Test
    void getImage() {
        // Initialize the database
        imageRepository.save(image).block();

        // Get the image
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, image.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(image.getId().intValue()))
            .jsonPath("$.imageDir")
            .value(is(DEFAULT_IMAGE_DIR));
    }

    @Test
    void getNonExistingImage() {
        // Get the image
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewImage() throws Exception {
        // Configure the mock search repository
        when(mockImageSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        imageRepository.save(image).block();

        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();

        // Update the image
        Image updatedImage = imageRepository.findById(image.getId()).block();
        updatedImage.imageDir(UPDATED_IMAGE_DIR);
        ImageDTO imageDTO = imageMapper.toDto(updatedImage);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, imageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getImageDir()).isEqualTo(UPDATED_IMAGE_DIR);

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository).save(testImage);
    }

    @Test
    void putNonExistingImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();
        image.setId(count.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, imageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(0)).save(image);
    }

    @Test
    void putWithIdMismatchImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();
        image.setId(count.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(0)).save(image);
    }

    @Test
    void putWithMissingIdPathParamImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();
        image.setId(count.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(0)).save(image);
    }

    @Test
    void partialUpdateImageWithPatch() throws Exception {
        // Initialize the database
        imageRepository.save(image).block();

        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();

        // Update the image using partial update
        Image partialUpdatedImage = new Image();
        partialUpdatedImage.setId(image.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedImage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedImage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getImageDir()).isEqualTo(DEFAULT_IMAGE_DIR);
    }

    @Test
    void fullUpdateImageWithPatch() throws Exception {
        // Initialize the database
        imageRepository.save(image).block();

        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();

        // Update the image using partial update
        Image partialUpdatedImage = new Image();
        partialUpdatedImage.setId(image.getId());

        partialUpdatedImage.imageDir(UPDATED_IMAGE_DIR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedImage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedImage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getImageDir()).isEqualTo(UPDATED_IMAGE_DIR);
    }

    @Test
    void patchNonExistingImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();
        image.setId(count.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, imageDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(0)).save(image);
    }

    @Test
    void patchWithIdMismatchImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();
        image.setId(count.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(0)).save(image);
    }

    @Test
    void patchWithMissingIdPathParamImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().collectList().block().size();
        image.setId(count.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(imageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(0)).save(image);
    }

    @Test
    void deleteImage() {
        // Configure the mock search repository
        when(mockImageSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockImageSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        imageRepository.save(image).block();

        int databaseSizeBeforeDelete = imageRepository.findAll().collectList().block().size();

        // Delete the image
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, image.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Image> imageList = imageRepository.findAll().collectList().block();
        assertThat(imageList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(1)).deleteById(image.getId());
    }

    @Test
    void searchImage() {
        // Configure the mock search repository
        when(mockImageSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        imageRepository.save(image).block();
        when(mockImageSearchRepository.search("id:" + image.getId())).thenReturn(Flux.just(image));

        // Search the image
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + image.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(image.getId().intValue()))
            .jsonPath("$.[*].imageDir")
            .value(hasItem(DEFAULT_IMAGE_DIR));
    }
}
