package com.test.captcha.service;

import com.test.captcha.service.dto.ItemStatusDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.test.captcha.domain.ItemStatus}.
 */
public interface ItemStatusService {
    /**
     * Save a itemStatus.
     *
     * @param itemStatusDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ItemStatusDTO> save(ItemStatusDTO itemStatusDTO);

    /**
     * Partially updates a itemStatus.
     *
     * @param itemStatusDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ItemStatusDTO> partialUpdate(ItemStatusDTO itemStatusDTO);

    /**
     * Get all the itemStatuses.
     *
     * @return the list of entities.
     */
    Flux<ItemStatusDTO> findAll();

    /**
     * Returns the number of itemStatuses available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of itemStatuses available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" itemStatus.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ItemStatusDTO> findOne(Long id);

    /**
     * Delete the "id" itemStatus.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the itemStatus corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<ItemStatusDTO> search(String query);
}
