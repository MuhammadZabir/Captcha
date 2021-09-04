package com.test.captcha.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.captcha.domain.PurchaseHistory;
import com.test.captcha.repository.PurchaseHistoryRepository;
import com.test.captcha.repository.search.PurchaseHistorySearchRepository;
import com.test.captcha.service.PurchaseHistoryService;
import com.test.captcha.service.dto.PurchaseHistoryDTO;
import com.test.captcha.service.mapper.PurchaseHistoryMapper;
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
 * Service Implementation for managing {@link PurchaseHistory}.
 */
@Service
@Transactional
public class PurchaseHistoryServiceImpl implements PurchaseHistoryService {

    private final Logger log = LoggerFactory.getLogger(PurchaseHistoryServiceImpl.class);

    private final PurchaseHistoryRepository purchaseHistoryRepository;

    private final PurchaseHistoryMapper purchaseHistoryMapper;

    private final PurchaseHistorySearchRepository purchaseHistorySearchRepository;

    public PurchaseHistoryServiceImpl(
        PurchaseHistoryRepository purchaseHistoryRepository,
        PurchaseHistoryMapper purchaseHistoryMapper,
        PurchaseHistorySearchRepository purchaseHistorySearchRepository
    ) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.purchaseHistoryMapper = purchaseHistoryMapper;
        this.purchaseHistorySearchRepository = purchaseHistorySearchRepository;
    }

    @Override
    public Mono<PurchaseHistoryDTO> save(PurchaseHistoryDTO purchaseHistoryDTO) {
        log.debug("Request to save PurchaseHistory : {}", purchaseHistoryDTO);
        return purchaseHistoryRepository
            .save(purchaseHistoryMapper.toEntity(purchaseHistoryDTO))
            .flatMap(purchaseHistorySearchRepository::save)
            .map(purchaseHistoryMapper::toDto);
    }

    @Override
    public Mono<PurchaseHistoryDTO> partialUpdate(PurchaseHistoryDTO purchaseHistoryDTO) {
        log.debug("Request to partially update PurchaseHistory : {}", purchaseHistoryDTO);

        return purchaseHistoryRepository
            .findById(purchaseHistoryDTO.getId())
            .map(
                existingPurchaseHistory -> {
                    purchaseHistoryMapper.partialUpdate(existingPurchaseHistory, purchaseHistoryDTO);

                    return existingPurchaseHistory;
                }
            )
            .flatMap(purchaseHistoryRepository::save)
            .flatMap(
                savedPurchaseHistory -> {
                    purchaseHistorySearchRepository.save(savedPurchaseHistory);

                    return Mono.just(savedPurchaseHistory);
                }
            )
            .map(purchaseHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PurchaseHistoryDTO> findAll() {
        log.debug("Request to get all PurchaseHistories");
        return purchaseHistoryRepository.findAll().map(purchaseHistoryMapper::toDto);
    }

    public Mono<Long> countAll() {
        return purchaseHistoryRepository.count();
    }

    public Mono<Long> searchCount() {
        return purchaseHistorySearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PurchaseHistoryDTO> findOne(Long id) {
        log.debug("Request to get PurchaseHistory : {}", id);
        return purchaseHistoryRepository.findById(id).map(purchaseHistoryMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete PurchaseHistory : {}", id);
        return purchaseHistoryRepository.deleteById(id).then(purchaseHistorySearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PurchaseHistoryDTO> search(String query) {
        log.debug("Request to search PurchaseHistories for query {}", query);
        return purchaseHistorySearchRepository.search(query).map(purchaseHistoryMapper::toDto);
    }
}
