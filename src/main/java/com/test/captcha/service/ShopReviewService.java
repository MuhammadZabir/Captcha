package com.test.captcha.service;

import com.test.captcha.service.dto.ShopReviewDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.test.captcha.domain.ShopReview}.
 */
public interface ShopReviewService {
    /**
     * Save a shopReview.
     *
     * @param shopReviewDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ShopReviewDTO> save(ShopReviewDTO shopReviewDTO);

    /**
     * Partially updates a shopReview.
     *
     * @param shopReviewDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ShopReviewDTO> partialUpdate(ShopReviewDTO shopReviewDTO);

    /**
     * Get all the shopReviews.
     *
     * @return the list of entities.
     */
    Flux<ShopReviewDTO> findAll();

    /**
     * Returns the number of shopReviews available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of shopReviews available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" shopReview.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ShopReviewDTO> findOne(Long id);

    /**
     * Delete the "id" shopReview.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the shopReview corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<ShopReviewDTO> search(String query);
}
