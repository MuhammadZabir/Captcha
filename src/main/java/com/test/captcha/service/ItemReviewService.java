package com.test.captcha.service;

import com.test.captcha.service.dto.ItemReviewDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.test.captcha.domain.ItemReview}.
 */
public interface ItemReviewService {
    /**
     * Save a itemReview.
     *
     * @param itemReviewDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ItemReviewDTO> save(ItemReviewDTO itemReviewDTO);

    /**
     * Partially updates a itemReview.
     *
     * @param itemReviewDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ItemReviewDTO> partialUpdate(ItemReviewDTO itemReviewDTO);

    /**
     * Get all the itemReviews.
     *
     * @return the list of entities.
     */
    Flux<ItemReviewDTO> findAll();

    /**
     * Returns the number of itemReviews available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of itemReviews available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" itemReview.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ItemReviewDTO> findOne(Long id);

    /**
     * Delete the "id" itemReview.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the itemReview corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<ItemReviewDTO> search(String query);
}
