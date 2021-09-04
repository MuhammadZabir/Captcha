package com.test.captcha.web.rest;

import com.test.captcha.repository.PurchaseHistoryRepository;
import com.test.captcha.service.PurchaseHistoryService;
import com.test.captcha.service.dto.PurchaseHistoryDTO;
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
 * REST controller for managing {@link com.test.captcha.domain.PurchaseHistory}.
 */
@RestController
@RequestMapping("/api")
public class PurchaseHistoryResource {

    private final Logger log = LoggerFactory.getLogger(PurchaseHistoryResource.class);

    private static final String ENTITY_NAME = "purchaseHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PurchaseHistoryService purchaseHistoryService;

    private final PurchaseHistoryRepository purchaseHistoryRepository;

    public PurchaseHistoryResource(PurchaseHistoryService purchaseHistoryService, PurchaseHistoryRepository purchaseHistoryRepository) {
        this.purchaseHistoryService = purchaseHistoryService;
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }

    /**
     * {@code POST  /purchase-histories} : Create a new purchaseHistory.
     *
     * @param purchaseHistoryDTO the purchaseHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new purchaseHistoryDTO, or with status {@code 400 (Bad Request)} if the purchaseHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/purchase-histories")
    public Mono<ResponseEntity<PurchaseHistoryDTO>> createPurchaseHistory(@RequestBody PurchaseHistoryDTO purchaseHistoryDTO)
        throws URISyntaxException {
        log.debug("REST request to save PurchaseHistory : {}", purchaseHistoryDTO);
        if (purchaseHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new purchaseHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return purchaseHistoryService
            .save(purchaseHistoryDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/purchase-histories/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /purchase-histories/:id} : Updates an existing purchaseHistory.
     *
     * @param id the id of the purchaseHistoryDTO to save.
     * @param purchaseHistoryDTO the purchaseHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the purchaseHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/purchase-histories/{id}")
    public Mono<ResponseEntity<PurchaseHistoryDTO>> updatePurchaseHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchaseHistoryDTO purchaseHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PurchaseHistory : {}, {}", id, purchaseHistoryDTO);
        if (purchaseHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return purchaseHistoryRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return purchaseHistoryService
                        .save(purchaseHistoryDTO)
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
     * {@code PATCH  /purchase-histories/:id} : Partial updates given fields of an existing purchaseHistory, field will ignore if it is null
     *
     * @param id the id of the purchaseHistoryDTO to save.
     * @param purchaseHistoryDTO the purchaseHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the purchaseHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the purchaseHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/purchase-histories/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PurchaseHistoryDTO>> partialUpdatePurchaseHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchaseHistoryDTO purchaseHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PurchaseHistory partially : {}, {}", id, purchaseHistoryDTO);
        if (purchaseHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return purchaseHistoryRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PurchaseHistoryDTO> result = purchaseHistoryService.partialUpdate(purchaseHistoryDTO);

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
     * {@code GET  /purchase-histories} : get all the purchaseHistories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of purchaseHistories in body.
     */
    @GetMapping("/purchase-histories")
    public Mono<List<PurchaseHistoryDTO>> getAllPurchaseHistories() {
        log.debug("REST request to get all PurchaseHistories");
        return purchaseHistoryService.findAll().collectList();
    }

    /**
     * {@code GET  /purchase-histories} : get all the purchaseHistories as a stream.
     * @return the {@link Flux} of purchaseHistories.
     */
    @GetMapping(value = "/purchase-histories", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PurchaseHistoryDTO> getAllPurchaseHistoriesAsStream() {
        log.debug("REST request to get all PurchaseHistories as a stream");
        return purchaseHistoryService.findAll();
    }

    /**
     * {@code GET  /purchase-histories/:id} : get the "id" purchaseHistory.
     *
     * @param id the id of the purchaseHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the purchaseHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/purchase-histories/{id}")
    public Mono<ResponseEntity<PurchaseHistoryDTO>> getPurchaseHistory(@PathVariable Long id) {
        log.debug("REST request to get PurchaseHistory : {}", id);
        Mono<PurchaseHistoryDTO> purchaseHistoryDTO = purchaseHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(purchaseHistoryDTO);
    }

    /**
     * {@code DELETE  /purchase-histories/:id} : delete the "id" purchaseHistory.
     *
     * @param id the id of the purchaseHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/purchase-histories/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePurchaseHistory(@PathVariable Long id) {
        log.debug("REST request to delete PurchaseHistory : {}", id);
        return purchaseHistoryService
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
     * {@code SEARCH  /_search/purchase-histories?query=:query} : search for the purchaseHistory corresponding
     * to the query.
     *
     * @param query the query of the purchaseHistory search.
     * @return the result of the search.
     */
    @GetMapping("/_search/purchase-histories")
    public Mono<List<PurchaseHistoryDTO>> searchPurchaseHistories(@RequestParam String query) {
        log.debug("REST request to search PurchaseHistories for query {}", query);
        return purchaseHistoryService.search(query).collectList();
    }
}
