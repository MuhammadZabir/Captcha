package com.test.captcha.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.captcha.domain.ItemReview;
import com.test.captcha.repository.ItemReviewRepository;
import com.test.captcha.repository.search.ItemReviewSearchRepository;
import com.test.captcha.service.ItemReviewService;
import com.test.captcha.service.dto.ItemReviewDTO;
import com.test.captcha.service.mapper.ItemReviewMapper;
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
 * Service Implementation for managing {@link ItemReview}.
 */
@Service
@Transactional
public class ItemReviewServiceImpl implements ItemReviewService {

    private final Logger log = LoggerFactory.getLogger(ItemReviewServiceImpl.class);

    private final ItemReviewRepository itemReviewRepository;

    private final ItemReviewMapper itemReviewMapper;

    private final ItemReviewSearchRepository itemReviewSearchRepository;

    public ItemReviewServiceImpl(
        ItemReviewRepository itemReviewRepository,
        ItemReviewMapper itemReviewMapper,
        ItemReviewSearchRepository itemReviewSearchRepository
    ) {
        this.itemReviewRepository = itemReviewRepository;
        this.itemReviewMapper = itemReviewMapper;
        this.itemReviewSearchRepository = itemReviewSearchRepository;
    }

    @Override
    public Mono<ItemReviewDTO> save(ItemReviewDTO itemReviewDTO) {
        log.debug("Request to save ItemReview : {}", itemReviewDTO);
        return itemReviewRepository
            .save(itemReviewMapper.toEntity(itemReviewDTO))
            .flatMap(itemReviewSearchRepository::save)
            .map(itemReviewMapper::toDto);
    }

    @Override
    public Mono<ItemReviewDTO> partialUpdate(ItemReviewDTO itemReviewDTO) {
        log.debug("Request to partially update ItemReview : {}", itemReviewDTO);

        return itemReviewRepository
            .findById(itemReviewDTO.getId())
            .map(
                existingItemReview -> {
                    itemReviewMapper.partialUpdate(existingItemReview, itemReviewDTO);

                    return existingItemReview;
                }
            )
            .flatMap(itemReviewRepository::save)
            .flatMap(
                savedItemReview -> {
                    itemReviewSearchRepository.save(savedItemReview);

                    return Mono.just(savedItemReview);
                }
            )
            .map(itemReviewMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemReviewDTO> findAll() {
        log.debug("Request to get all ItemReviews");
        return itemReviewRepository.findAll().map(itemReviewMapper::toDto);
    }

    public Mono<Long> countAll() {
        return itemReviewRepository.count();
    }

    public Mono<Long> searchCount() {
        return itemReviewSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemReviewDTO> findOne(Long id) {
        log.debug("Request to get ItemReview : {}", id);
        return itemReviewRepository.findById(id).map(itemReviewMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ItemReview : {}", id);
        return itemReviewRepository.deleteById(id).then(itemReviewSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemReviewDTO> search(String query) {
        log.debug("Request to search ItemReviews for query {}", query);
        return itemReviewSearchRepository.search(query).map(itemReviewMapper::toDto);
    }
}
