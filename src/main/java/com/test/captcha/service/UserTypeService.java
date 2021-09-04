package com.test.captcha.service;

import com.test.captcha.service.dto.UserTypeDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.test.captcha.domain.UserType}.
 */
public interface UserTypeService {
    /**
     * Save a userType.
     *
     * @param userTypeDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<UserTypeDTO> save(UserTypeDTO userTypeDTO);

    /**
     * Partially updates a userType.
     *
     * @param userTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<UserTypeDTO> partialUpdate(UserTypeDTO userTypeDTO);

    /**
     * Get all the userTypes.
     *
     * @return the list of entities.
     */
    Flux<UserTypeDTO> findAll();

    /**
     * Returns the number of userTypes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of userTypes available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" userType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<UserTypeDTO> findOne(Long id);

    /**
     * Delete the "id" userType.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the userType corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<UserTypeDTO> search(String query);
}
