package com.test.captcha.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.captcha.domain.UserExtra;
import com.test.captcha.repository.UserExtraRepository;
import com.test.captcha.repository.search.UserExtraSearchRepository;
import com.test.captcha.service.UserExtraService;
import com.test.captcha.service.dto.UserExtraDTO;
import com.test.captcha.service.mapper.UserExtraMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link UserExtra}.
 */
@Service
@Transactional
public class UserExtraServiceImpl implements UserExtraService {

    private final Logger log = LoggerFactory.getLogger(UserExtraServiceImpl.class);

    private final UserExtraRepository userExtraRepository;

    private final UserExtraMapper userExtraMapper;

    private final UserExtraSearchRepository userExtraSearchRepository;

    public UserExtraServiceImpl(
        UserExtraRepository userExtraRepository,
        UserExtraMapper userExtraMapper,
        UserExtraSearchRepository userExtraSearchRepository
    ) {
        this.userExtraRepository = userExtraRepository;
        this.userExtraMapper = userExtraMapper;
        this.userExtraSearchRepository = userExtraSearchRepository;
    }

    @Override
    public Mono<UserExtraDTO> save(UserExtraDTO userExtraDTO) {
        log.debug("Request to save UserExtra : {}", userExtraDTO);
        return userExtraRepository
            .save(userExtraMapper.toEntity(userExtraDTO))
            .flatMap(userExtraSearchRepository::save)
            .map(userExtraMapper::toDto);
    }

    @Override
    public Mono<UserExtraDTO> partialUpdate(UserExtraDTO userExtraDTO) {
        log.debug("Request to partially update UserExtra : {}", userExtraDTO);

        return userExtraRepository
            .findById(userExtraDTO.getId())
            .map(
                existingUserExtra -> {
                    userExtraMapper.partialUpdate(existingUserExtra, userExtraDTO);

                    return existingUserExtra;
                }
            )
            .flatMap(userExtraRepository::save)
            .flatMap(
                savedUserExtra -> {
                    userExtraSearchRepository.save(savedUserExtra);

                    return Mono.just(savedUserExtra);
                }
            )
            .map(userExtraMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserExtraDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UserExtras");
        return userExtraRepository.findAllBy(pageable).map(userExtraMapper::toDto);
    }

    public Mono<Long> countAll() {
        return userExtraRepository.count();
    }

    public Mono<Long> searchCount() {
        return userExtraSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserExtraDTO> findOne(Long id) {
        log.debug("Request to get UserExtra : {}", id);
        return userExtraRepository.findById(id).map(userExtraMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete UserExtra : {}", id);
        return userExtraRepository.deleteById(id).then(userExtraSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserExtraDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UserExtras for query {}", query);
        return userExtraSearchRepository.search(query, pageable).map(userExtraMapper::toDto);
    }
}
