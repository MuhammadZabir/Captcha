package com.test.captcha.service;

import com.test.captcha.service.dto.PurchaseHistoryDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.test.captcha.domain.PurchaseHistory}.
 */
public interface PurchaseHistoryService {
    /**
     * Save a purchaseHistory.
     *
     * @param purchaseHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PurchaseHistoryDTO> save(PurchaseHistoryDTO purchaseHistoryDTO);

    /**
     * Partially updates a purchaseHistory.
     *
     * @param purchaseHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PurchaseHistoryDTO> partialUpdate(PurchaseHistoryDTO purchaseHistoryDTO);

    /**
     * Get all the purchaseHistories.
     *
     * @return the list of entities.
     */
    Flux<PurchaseHistoryDTO> findAll();

    /**
     * Returns the number of purchaseHistories available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of purchaseHistories available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" purchaseHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PurchaseHistoryDTO> findOne(Long id);

    /**
     * Delete the "id" purchaseHistory.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the purchaseHistory corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<PurchaseHistoryDTO> search(String query);
}
