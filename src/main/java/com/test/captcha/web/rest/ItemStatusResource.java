package com.test.captcha.web.rest;

import com.test.captcha.repository.ItemStatusRepository;
import com.test.captcha.service.ItemStatusService;
import com.test.captcha.service.dto.ItemStatusDTO;
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
 * REST controller for managing {@link com.test.captcha.domain.ItemStatus}.
 */
@RestController
@RequestMapping("/api")
public class ItemStatusResource {

    private final Logger log = LoggerFactory.getLogger(ItemStatusResource.class);

    private static final String ENTITY_NAME = "itemStatus";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ItemStatusService itemStatusService;

    private final ItemStatusRepository itemStatusRepository;

    public ItemStatusResource(ItemStatusService itemStatusService, ItemStatusRepository itemStatusRepository) {
        this.itemStatusService = itemStatusService;
        this.itemStatusRepository = itemStatusRepository;
    }

    /**
     * {@code POST  /item-statuses} : Create a new itemStatus.
     *
     * @param itemStatusDTO the itemStatusDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new itemStatusDTO, or with status {@code 400 (Bad Request)} if the itemStatus has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/item-statuses")
    public Mono<ResponseEntity<ItemStatusDTO>> createItemStatus(@RequestBody ItemStatusDTO itemStatusDTO) throws URISyntaxException {
        log.debug("REST request to save ItemStatus : {}", itemStatusDTO);
        if (itemStatusDTO.getId() != null) {
            throw new BadRequestAlertException("A new itemStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return itemStatusService
            .save(itemStatusDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/item-statuses/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /item-statuses/:id} : Updates an existing itemStatus.
     *
     * @param id the id of the itemStatusDTO to save.
     * @param itemStatusDTO the itemStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemStatusDTO,
     * or with status {@code 400 (Bad Request)} if the itemStatusDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the itemStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/item-statuses/{id}")
    public Mono<ResponseEntity<ItemStatusDTO>> updateItemStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ItemStatusDTO itemStatusDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ItemStatus : {}, {}", id, itemStatusDTO);
        if (itemStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return itemStatusRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return itemStatusService
                        .save(itemStatusDTO)
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
     * {@code PATCH  /item-statuses/:id} : Partial updates given fields of an existing itemStatus, field will ignore if it is null
     *
     * @param id the id of the itemStatusDTO to save.
     * @param itemStatusDTO the itemStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemStatusDTO,
     * or with status {@code 400 (Bad Request)} if the itemStatusDTO is not valid,
     * or with status {@code 404 (Not Found)} if the itemStatusDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the itemStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/item-statuses/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<ItemStatusDTO>> partialUpdateItemStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ItemStatusDTO itemStatusDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ItemStatus partially : {}, {}", id, itemStatusDTO);
        if (itemStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return itemStatusRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<ItemStatusDTO> result = itemStatusService.partialUpdate(itemStatusDTO);

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
     * {@code GET  /item-statuses} : get all the itemStatuses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of itemStatuses in body.
     */
    @GetMapping("/item-statuses")
    public Mono<List<ItemStatusDTO>> getAllItemStatuses() {
        log.debug("REST request to get all ItemStatuses");
        return itemStatusService.findAll().collectList();
    }

    /**
     * {@code GET  /item-statuses} : get all the itemStatuses as a stream.
     * @return the {@link Flux} of itemStatuses.
     */
    @GetMapping(value = "/item-statuses", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ItemStatusDTO> getAllItemStatusesAsStream() {
        log.debug("REST request to get all ItemStatuses as a stream");
        return itemStatusService.findAll();
    }

    /**
     * {@code GET  /item-statuses/:id} : get the "id" itemStatus.
     *
     * @param id the id of the itemStatusDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the itemStatusDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/item-statuses/{id}")
    public Mono<ResponseEntity<ItemStatusDTO>> getItemStatus(@PathVariable Long id) {
        log.debug("REST request to get ItemStatus : {}", id);
        Mono<ItemStatusDTO> itemStatusDTO = itemStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(itemStatusDTO);
    }

    /**
     * {@code DELETE  /item-statuses/:id} : delete the "id" itemStatus.
     *
     * @param id the id of the itemStatusDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/item-statuses/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteItemStatus(@PathVariable Long id) {
        log.debug("REST request to delete ItemStatus : {}", id);
        return itemStatusService
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
     * {@code SEARCH  /_search/item-statuses?query=:query} : search for the itemStatus corresponding
     * to the query.
     *
     * @param query the query of the itemStatus search.
     * @return the result of the search.
     */
    @GetMapping("/_search/item-statuses")
    public Mono<List<ItemStatusDTO>> searchItemStatuses(@RequestParam String query) {
        log.debug("REST request to search ItemStatuses for query {}", query);
        return itemStatusService.search(query).collectList();
    }
}
