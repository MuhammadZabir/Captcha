package com.test.captcha.web.rest;

import com.test.captcha.repository.ShopReviewRepository;
import com.test.captcha.service.ShopReviewService;
import com.test.captcha.service.dto.ShopReviewDTO;
import com.test.captcha.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.test.captcha.domain.ShopReview}.
 */
@RestController
@RequestMapping("/api")
public class ShopReviewResource {

    private final Logger log = LoggerFactory.getLogger(ShopReviewResource.class);

    private static final String ENTITY_NAME = "shopReview";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShopReviewService shopReviewService;

    private final ShopReviewRepository shopReviewRepository;

    public ShopReviewResource(ShopReviewService shopReviewService, ShopReviewRepository shopReviewRepository) {
        this.shopReviewService = shopReviewService;
        this.shopReviewRepository = shopReviewRepository;
    }

    /**
     * {@code POST  /shop-reviews} : Create a new shopReview.
     *
     * @param shopReviewDTO the shopReviewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shopReviewDTO, or with status {@code 400 (Bad Request)} if the shopReview has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/shop-reviews")
    public Mono<ResponseEntity<ShopReviewDTO>> createShopReview(@RequestBody ShopReviewDTO shopReviewDTO) throws URISyntaxException {
        log.debug("REST request to save ShopReview : {}", shopReviewDTO);
        if (shopReviewDTO.getId() != null) {
            throw new BadRequestAlertException("A new shopReview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return shopReviewService
            .save(shopReviewDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/shop-reviews/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /shop-reviews/:id} : Updates an existing shopReview.
     *
     * @param id the id of the shopReviewDTO to save.
     * @param shopReviewDTO the shopReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shopReviewDTO,
     * or with status {@code 400 (Bad Request)} if the shopReviewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shopReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/shop-reviews/{id}")
    public Mono<ResponseEntity<ShopReviewDTO>> updateShopReview(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ShopReviewDTO shopReviewDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ShopReview : {}, {}", id, shopReviewDTO);
        if (shopReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shopReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return shopReviewRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return shopReviewService
                        .save(shopReviewDTO)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /shop-reviews/:id} : Partial updates given fields of an existing shopReview, field will ignore if it is null
     *
     * @param id the id of the shopReviewDTO to save.
     * @param shopReviewDTO the shopReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shopReviewDTO,
     * or with status {@code 400 (Bad Request)} if the shopReviewDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shopReviewDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shopReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/shop-reviews/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<ShopReviewDTO>> partialUpdateShopReview(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ShopReviewDTO shopReviewDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShopReview partially : {}, {}", id, shopReviewDTO);
        if (shopReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shopReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return shopReviewRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<ShopReviewDTO> result = shopReviewService.partialUpdate(shopReviewDTO);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString())
                                    )
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /shop-reviews} : get all the shopReviews.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shopReviews in body.
     */
    @GetMapping("/shop-reviews")
    public Mono<List<ShopReviewDTO>> getAllShopReviews() {
        log.debug("REST request to get all ShopReviews");
        return shopReviewService.findAll().collectList();
    }

    /**
     * {@code GET  /shop-reviews} : get all the shopReviews as a stream.
     * @return the {@link Flux} of shopReviews.
     */
    @GetMapping(value = "/shop-reviews", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ShopReviewDTO> getAllShopReviewsAsStream() {
        log.debug("REST request to get all ShopReviews as a stream");
        return shopReviewService.findAll();
    }

    /**
     * {@code GET  /shop-reviews/:id} : get the "id" shopReview.
     *
     * @param id the id of the shopReviewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shopReviewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/shop-reviews/{id}")
    public Mono<ResponseEntity<ShopReviewDTO>> getShopReview(@PathVariable Long id) {
        log.debug("REST request to get ShopReview : {}", id);
        Mono<ShopReviewDTO> shopReviewDTO = shopReviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shopReviewDTO);
    }

    /**
     * {@code DELETE  /shop-reviews/:id} : delete the "id" shopReview.
     *
     * @param id the id of the shopReviewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/shop-reviews/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteShopReview(@PathVariable Long id) {
        log.debug("REST request to delete ShopReview : {}", id);
        return shopReviewService
            .delete(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
            );
    }

    /**
     * {@code SEARCH  /_search/shop-reviews?query=:query} : search for the shopReview corresponding
     * to the query.
     *
     * @param query the query of the shopReview search.
     * @return the result of the search.
     */
    @GetMapping("/_search/shop-reviews")
    public Mono<List<ShopReviewDTO>> searchShopReviews(@RequestParam String query) {
        log.debug("REST request to search ShopReviews for query {}", query);
        return shopReviewService.search(query).collectList();
    }
}
