package com.test.captcha.web.rest;

import com.test.captcha.repository.ItemReviewRepository;
import com.test.captcha.service.ItemReviewService;
import com.test.captcha.service.dto.ItemReviewDTO;
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
 * REST controller for managing {@link com.test.captcha.domain.ItemReview}.
 */
@RestController
@RequestMapping("/api")
public class ItemReviewResource {

    private final Logger log = LoggerFactory.getLogger(ItemReviewResource.class);

    private static final String ENTITY_NAME = "itemReview";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ItemReviewService itemReviewService;

    private final ItemReviewRepository itemReviewRepository;

    public ItemReviewResource(ItemReviewService itemReviewService, ItemReviewRepository itemReviewRepository) {
        this.itemReviewService = itemReviewService;
        this.itemReviewRepository = itemReviewRepository;
    }

    /**
     * {@code POST  /item-reviews} : Create a new itemReview.
     *
     * @param itemReviewDTO the itemReviewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new itemReviewDTO, or with status {@code 400 (Bad Request)} if the itemReview has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/item-reviews")
    public Mono<ResponseEntity<ItemReviewDTO>> createItemReview(@RequestBody ItemReviewDTO itemReviewDTO) throws URISyntaxException {
        log.debug("REST request to save ItemReview : {}", itemReviewDTO);
        if (itemReviewDTO.getId() != null) {
            throw new BadRequestAlertException("A new itemReview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return itemReviewService
            .save(itemReviewDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/item-reviews/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /item-reviews/:id} : Updates an existing itemReview.
     *
     * @param id the id of the itemReviewDTO to save.
     * @param itemReviewDTO the itemReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemReviewDTO,
     * or with status {@code 400 (Bad Request)} if the itemReviewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the itemReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/item-reviews/{id}")
    public Mono<ResponseEntity<ItemReviewDTO>> updateItemReview(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ItemReviewDTO itemReviewDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ItemReview : {}, {}", id, itemReviewDTO);
        if (itemReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return itemReviewRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return itemReviewService
                        .save(itemReviewDTO)
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
     * {@code PATCH  /item-reviews/:id} : Partial updates given fields of an existing itemReview, field will ignore if it is null
     *
     * @param id the id of the itemReviewDTO to save.
     * @param itemReviewDTO the itemReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemReviewDTO,
     * or with status {@code 400 (Bad Request)} if the itemReviewDTO is not valid,
     * or with status {@code 404 (Not Found)} if the itemReviewDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the itemReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/item-reviews/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<ItemReviewDTO>> partialUpdateItemReview(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ItemReviewDTO itemReviewDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ItemReview partially : {}, {}", id, itemReviewDTO);
        if (itemReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return itemReviewRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<ItemReviewDTO> result = itemReviewService.partialUpdate(itemReviewDTO);

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
     * {@code GET  /item-reviews} : get all the itemReviews.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of itemReviews in body.
     */
    @GetMapping("/item-reviews")
    public Mono<List<ItemReviewDTO>> getAllItemReviews() {
        log.debug("REST request to get all ItemReviews");
        return itemReviewService.findAll().collectList();
    }

    /**
     * {@code GET  /item-reviews} : get all the itemReviews as a stream.
     * @return the {@link Flux} of itemReviews.
     */
    @GetMapping(value = "/item-reviews", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ItemReviewDTO> getAllItemReviewsAsStream() {
        log.debug("REST request to get all ItemReviews as a stream");
        return itemReviewService.findAll();
    }

    /**
     * {@code GET  /item-reviews/:id} : get the "id" itemReview.
     *
     * @param id the id of the itemReviewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the itemReviewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/item-reviews/{id}")
    public Mono<ResponseEntity<ItemReviewDTO>> getItemReview(@PathVariable Long id) {
        log.debug("REST request to get ItemReview : {}", id);
        Mono<ItemReviewDTO> itemReviewDTO = itemReviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(itemReviewDTO);
    }

    /**
     * {@code DELETE  /item-reviews/:id} : delete the "id" itemReview.
     *
     * @param id the id of the itemReviewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/item-reviews/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteItemReview(@PathVariable Long id) {
        log.debug("REST request to delete ItemReview : {}", id);
        return itemReviewService
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
     * {@code SEARCH  /_search/item-reviews?query=:query} : search for the itemReview corresponding
     * to the query.
     *
     * @param query the query of the itemReview search.
     * @return the result of the search.
     */
    @GetMapping("/_search/item-reviews")
    public Mono<List<ItemReviewDTO>> searchItemReviews(@RequestParam String query) {
        log.debug("REST request to search ItemReviews for query {}", query);
        return itemReviewService.search(query).collectList();
    }
}
