package com.test.captcha.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.captcha.domain.UserType;
import com.test.captcha.repository.UserTypeRepository;
import com.test.captcha.repository.search.UserTypeSearchRepository;
import com.test.captcha.service.UserTypeService;
import com.test.captcha.service.dto.UserTypeDTO;
import com.test.captcha.service.mapper.UserTypeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link UserType}.
 */
@Service
@Transactional
public class UserTypeServiceImpl implements UserTypeService {

    private final Logger log = LoggerFactory.getLogger(UserTypeServiceImpl.class);

    private final UserTypeRepository userTypeRepository;

    private final UserTypeMapper userTypeMapper;

    private final UserTypeSearchRepository userTypeSearchRepository;

    public UserTypeServiceImpl(
        UserTypeRepository userTypeRepository,
        UserTypeMapper userTypeMapper,
        UserTypeSearchRepository userTypeSearchRepository
    ) {
        this.userTypeRepository = userTypeRepository;
        this.userTypeMapper = userTypeMapper;
        this.userTypeSearchRepository = userTypeSearchRepository;
    }

    @Override
    public Mono<UserTypeDTO> save(UserTypeDTO userTypeDTO) {
        log.debug("Request to save UserType : {}", userTypeDTO);
        return userTypeRepository
            .save(userTypeMapper.toEntity(userTypeDTO))
            .flatMap(userTypeSearchRepository::save)
            .map(userTypeMapper::toDto);
    }

    @Override
    public Mono<UserTypeDTO> partialUpdate(UserTypeDTO userTypeDTO) {
        log.debug("Request to partially update UserType : {}", userTypeDTO);

        return userTypeRepository
            .findById(userTypeDTO.getId())
            .map(
                existingUserType -> {
                    userTypeMapper.partialUpdate(existingUserType, userTypeDTO);

                    return existingUserType;
                }
            )
            .flatMap(userTypeRepository::save)
            .flatMap(
                savedUserType -> {
                    userTypeSearchRepository.save(savedUserType);

                    return Mono.just(savedUserType);
                }
            )
            .map(userTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserTypeDTO> findAll() {
        log.debug("Request to get all UserTypes");
        return userTypeRepository.findAll().map(userTypeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return userTypeRepository.count();
    }

    public Mono<Long> searchCount() {
        return userTypeSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserTypeDTO> findOne(Long id) {
        log.debug("Request to get UserType : {}", id);
        return userTypeRepository.findById(id).map(userTypeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete UserType : {}", id);
        return userTypeRepository.deleteById(id).then(userTypeSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserTypeDTO> search(String query) {
        log.debug("Request to search UserTypes for query {}", query);
        return userTypeSearchRepository.search(query).map(userTypeMapper::toDto);
    }
}
