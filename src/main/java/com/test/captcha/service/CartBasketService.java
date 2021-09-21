package com.test.captcha.service;

import com.test.captcha.service.dto.CartBasketDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.test.captcha.domain.CartBasket}.
 */
public interface CartBasketService {
    /**
     * Save a cartBasket.
     *
     * @param cartBasketDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CartBasketDTO> save(CartBasketDTO cartBasketDTO);

    /**
     * Partially updates a cartBasket.
     *
     * @param cartBasketDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CartBasketDTO> partialUpdate(CartBasketDTO cartBasketDTO);

    /**
     * Get all the cartBaskets.
     *
     * @return the list of entities.
     */
    Flux<CartBasketDTO> findAll();

    /**
     * Returns the number of cartBaskets available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of cartBaskets available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" cartBasket.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CartBasketDTO> findOne(Long id);

    /**
     * Delete the "id" cartBasket.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the cartBasket corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<CartBasketDTO> search(String query);

    /**
     * Get all CartBaskets by Cart Id.
     *
     * @param id the id of Cart entity.
     * @return
     */
    Flux<CartBasketDTO> findAllByCartId(Long id);

    /**
     * Get all CartBaskets by Item Id.
     *
     * @param id the id of Item entity.
     * @return
     */
    Flux<CartBasketDTO> findAllByItemId(Long id);
}
