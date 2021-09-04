package com.test.captcha.web.rest;

import com.test.captcha.repository.CartBasketRepository;
import com.test.captcha.service.CartBasketService;
import com.test.captcha.service.dto.CartBasketDTO;
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
 * REST controller for managing {@link com.test.captcha.domain.CartBasket}.
 */
@RestController
@RequestMapping("/api")
public class CartBasketResource {

    private final Logger log = LoggerFactory.getLogger(CartBasketResource.class);

    private static final String ENTITY_NAME = "cartBasket";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartBasketService cartBasketService;

    private final CartBasketRepository cartBasketRepository;

    public CartBasketResource(CartBasketService cartBasketService, CartBasketRepository cartBasketRepository) {
        this.cartBasketService = cartBasketService;
        this.cartBasketRepository = cartBasketRepository;
    }

    /**
     * {@code POST  /cart-baskets} : Create a new cartBasket.
     *
     * @param cartBasketDTO the cartBasketDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cartBasketDTO, or with status {@code 400 (Bad Request)} if the cartBasket has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cart-baskets")
    public Mono<ResponseEntity<CartBasketDTO>> createCartBasket(@RequestBody CartBasketDTO cartBasketDTO) throws URISyntaxException {
        log.debug("REST request to save CartBasket : {}", cartBasketDTO);
        if (cartBasketDTO.getId() != null) {
            throw new BadRequestAlertException("A new cartBasket cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return cartBasketService
            .save(cartBasketDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/cart-baskets/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /cart-baskets/:id} : Updates an existing cartBasket.
     *
     * @param id the id of the cartBasketDTO to save.
     * @param cartBasketDTO the cartBasketDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartBasketDTO,
     * or with status {@code 400 (Bad Request)} if the cartBasketDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cartBasketDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cart-baskets/{id}")
    public Mono<ResponseEntity<CartBasketDTO>> updateCartBasket(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CartBasketDTO cartBasketDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CartBasket : {}, {}", id, cartBasketDTO);
        if (cartBasketDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cartBasketDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cartBasketRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return cartBasketService
                        .save(cartBasketDTO)
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
     * {@code PATCH  /cart-baskets/:id} : Partial updates given fields of an existing cartBasket, field will ignore if it is null
     *
     * @param id the id of the cartBasketDTO to save.
     * @param cartBasketDTO the cartBasketDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartBasketDTO,
     * or with status {@code 400 (Bad Request)} if the cartBasketDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cartBasketDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cartBasketDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cart-baskets/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<CartBasketDTO>> partialUpdateCartBasket(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CartBasketDTO cartBasketDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CartBasket partially : {}, {}", id, cartBasketDTO);
        if (cartBasketDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cartBasketDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cartBasketRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<CartBasketDTO> result = cartBasketService.partialUpdate(cartBasketDTO);

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
     * {@code GET  /cart-baskets} : get all the cartBaskets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cartBaskets in body.
     */
    @GetMapping("/cart-baskets")
    public Mono<List<CartBasketDTO>> getAllCartBaskets() {
        log.debug("REST request to get all CartBaskets");
        return cartBasketService.findAll().collectList();
    }

    /**
     * {@code GET  /cart-baskets} : get all the cartBaskets as a stream.
     * @return the {@link Flux} of cartBaskets.
     */
    @GetMapping(value = "/cart-baskets", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<CartBasketDTO> getAllCartBasketsAsStream() {
        log.debug("REST request to get all CartBaskets as a stream");
        return cartBasketService.findAll();
    }

    /**
     * {@code GET  /cart-baskets/:id} : get the "id" cartBasket.
     *
     * @param id the id of the cartBasketDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cartBasketDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cart-baskets/{id}")
    public Mono<ResponseEntity<CartBasketDTO>> getCartBasket(@PathVariable Long id) {
        log.debug("REST request to get CartBasket : {}", id);
        Mono<CartBasketDTO> cartBasketDTO = cartBasketService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cartBasketDTO);
    }

    /**
     * {@code DELETE  /cart-baskets/:id} : delete the "id" cartBasket.
     *
     * @param id the id of the cartBasketDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cart-baskets/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCartBasket(@PathVariable Long id) {
        log.debug("REST request to delete CartBasket : {}", id);
        return cartBasketService
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
     * {@code SEARCH  /_search/cart-baskets?query=:query} : search for the cartBasket corresponding
     * to the query.
     *
     * @param query the query of the cartBasket search.
     * @return the result of the search.
     */
    @GetMapping("/_search/cart-baskets")
    public Mono<List<CartBasketDTO>> searchCartBaskets(@RequestParam String query) {
        log.debug("REST request to search CartBaskets for query {}", query);
        return cartBasketService.search(query).collectList();
    }
}
