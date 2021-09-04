package com.test.captcha.service;

import com.test.captcha.service.dto.ShopDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.test.captcha.domain.Shop}.
 */
public interface ShopService {
    /**
     * Save a shop.
     *
     * @param shopDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ShopDTO> save(ShopDTO shopDTO);

    /**
     * Partially updates a shop.
     *
     * @param shopDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ShopDTO> partialUpdate(ShopDTO shopDTO);

    /**
     * Get all the shops.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ShopDTO> findAll(Pageable pageable);

    /**
     * Returns the number of shops available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of shops available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" shop.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ShopDTO> findOne(Long id);

    /**
     * Delete the "id" shop.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the shop corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ShopDTO> search(String query, Pageable pageable);
}
