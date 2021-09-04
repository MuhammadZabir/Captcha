package com.test.captcha.web.rest;

import com.test.captcha.repository.UserTypeRepository;
import com.test.captcha.service.UserTypeService;
import com.test.captcha.service.dto.UserTypeDTO;
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
 * REST controller for managing {@link com.test.captcha.domain.UserType}.
 */
@RestController
@RequestMapping("/api")
public class UserTypeResource {

    private final Logger log = LoggerFactory.getLogger(UserTypeResource.class);

    private static final String ENTITY_NAME = "userType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserTypeService userTypeService;

    private final UserTypeRepository userTypeRepository;

    public UserTypeResource(UserTypeService userTypeService, UserTypeRepository userTypeRepository) {
        this.userTypeService = userTypeService;
        this.userTypeRepository = userTypeRepository;
    }

    /**
     * {@code POST  /user-types} : Create a new userType.
     *
     * @param userTypeDTO the userTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userTypeDTO, or with status {@code 400 (Bad Request)} if the userType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-types")
    public Mono<ResponseEntity<UserTypeDTO>> createUserType(@RequestBody UserTypeDTO userTypeDTO) throws URISyntaxException {
        log.debug("REST request to save UserType : {}", userTypeDTO);
        if (userTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new userType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return userTypeService
            .save(userTypeDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/user-types/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /user-types/:id} : Updates an existing userType.
     *
     * @param id the id of the userTypeDTO to save.
     * @param userTypeDTO the userTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userTypeDTO,
     * or with status {@code 400 (Bad Request)} if the userTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-types/{id}")
    public Mono<ResponseEntity<UserTypeDTO>> updateUserType(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserTypeDTO userTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UserType : {}, {}", id, userTypeDTO);
        if (userTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userTypeRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return userTypeService
                        .save(userTypeDTO)
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
     * {@code PATCH  /user-types/:id} : Partial updates given fields of an existing userType, field will ignore if it is null
     *
     * @param id the id of the userTypeDTO to save.
     * @param userTypeDTO the userTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userTypeDTO,
     * or with status {@code 400 (Bad Request)} if the userTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-types/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<UserTypeDTO>> partialUpdateUserType(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserTypeDTO userTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserType partially : {}, {}", id, userTypeDTO);
        if (userTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userTypeRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<UserTypeDTO> result = userTypeService.partialUpdate(userTypeDTO);

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
     * {@code GET  /user-types} : get all the userTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userTypes in body.
     */
    @GetMapping("/user-types")
    public Mono<List<UserTypeDTO>> getAllUserTypes() {
        log.debug("REST request to get all UserTypes");
        return userTypeService.findAll().collectList();
    }

    /**
     * {@code GET  /user-types} : get all the userTypes as a stream.
     * @return the {@link Flux} of userTypes.
     */
    @GetMapping(value = "/user-types", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<UserTypeDTO> getAllUserTypesAsStream() {
        log.debug("REST request to get all UserTypes as a stream");
        return userTypeService.findAll();
    }

    /**
     * {@code GET  /user-types/:id} : get the "id" userType.
     *
     * @param id the id of the userTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-types/{id}")
    public Mono<ResponseEntity<UserTypeDTO>> getUserType(@PathVariable Long id) {
        log.debug("REST request to get UserType : {}", id);
        Mono<UserTypeDTO> userTypeDTO = userTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userTypeDTO);
    }

    /**
     * {@code DELETE  /user-types/:id} : delete the "id" userType.
     *
     * @param id the id of the userTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-types/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteUserType(@PathVariable Long id) {
        log.debug("REST request to delete UserType : {}", id);
        return userTypeService
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
     * {@code SEARCH  /_search/user-types?query=:query} : search for the userType corresponding
     * to the query.
     *
     * @param query the query of the userType search.
     * @return the result of the search.
     */
    @GetMapping("/_search/user-types")
    public Mono<List<UserTypeDTO>> searchUserTypes(@RequestParam String query) {
        log.debug("REST request to search UserTypes for query {}", query);
        return userTypeService.search(query).collectList();
    }
}
