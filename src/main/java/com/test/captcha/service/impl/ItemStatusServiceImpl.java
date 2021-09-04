package com.test.captcha.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.captcha.domain.ItemStatus;
import com.test.captcha.repository.ItemStatusRepository;
import com.test.captcha.repository.search.ItemStatusSearchRepository;
import com.test.captcha.service.ItemStatusService;
import com.test.captcha.service.dto.ItemStatusDTO;
import com.test.captcha.service.mapper.ItemStatusMapper;
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
 * Service Implementation for managing {@link ItemStatus}.
 */
@Service
@Transactional
public class ItemStatusServiceImpl implements ItemStatusService {

    private final Logger log = LoggerFactory.getLogger(ItemStatusServiceImpl.class);

    private final ItemStatusRepository itemStatusRepository;

    private final ItemStatusMapper itemStatusMapper;

    private final ItemStatusSearchRepository itemStatusSearchRepository;

    public ItemStatusServiceImpl(
        ItemStatusRepository itemStatusRepository,
        ItemStatusMapper itemStatusMapper,
        ItemStatusSearchRepository itemStatusSearchRepository
    ) {
        this.itemStatusRepository = itemStatusRepository;
        this.itemStatusMapper = itemStatusMapper;
        this.itemStatusSearchRepository = itemStatusSearchRepository;
    }

    @Override
    public Mono<ItemStatusDTO> save(ItemStatusDTO itemStatusDTO) {
        log.debug("Request to save ItemStatus : {}", itemStatusDTO);
        return itemStatusRepository
            .save(itemStatusMapper.toEntity(itemStatusDTO))
            .flatMap(itemStatusSearchRepository::save)
            .map(itemStatusMapper::toDto);
    }

    @Override
    public Mono<ItemStatusDTO> partialUpdate(ItemStatusDTO itemStatusDTO) {
        log.debug("Request to partially update ItemStatus : {}", itemStatusDTO);

        return itemStatusRepository
            .findById(itemStatusDTO.getId())
            .map(
                existingItemStatus -> {
                    itemStatusMapper.partialUpdate(existingItemStatus, itemStatusDTO);

                    return existingItemStatus;
                }
            )
            .flatMap(itemStatusRepository::save)
            .flatMap(
                savedItemStatus -> {
                    itemStatusSearchRepository.save(savedItemStatus);

                    return Mono.just(savedItemStatus);
                }
            )
            .map(itemStatusMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemStatusDTO> findAll() {
        log.debug("Request to get all ItemStatuses");
        return itemStatusRepository.findAll().map(itemStatusMapper::toDto);
    }

    public Mono<Long> countAll() {
        return itemStatusRepository.count();
    }

    public Mono<Long> searchCount() {
        return itemStatusSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemStatusDTO> findOne(Long id) {
        log.debug("Request to get ItemStatus : {}", id);
        return itemStatusRepository.findById(id).map(itemStatusMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ItemStatus : {}", id);
        return itemStatusRepository.deleteById(id).then(itemStatusSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemStatusDTO> search(String query) {
        log.debug("Request to search ItemStatuses for query {}", query);
        return itemStatusSearchRepository.search(query).map(itemStatusMapper::toDto);
    }
}
