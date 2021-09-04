package com.test.captcha.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.captcha.domain.ShopReview;
import com.test.captcha.repository.ShopReviewRepository;
import com.test.captcha.repository.search.ShopReviewSearchRepository;
import com.test.captcha.service.ShopReviewService;
import com.test.captcha.service.dto.ShopReviewDTO;
import com.test.captcha.service.mapper.ShopReviewMapper;
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
 * Service Implementation for managing {@link ShopReview}.
 */
@Service
@Transactional
public class ShopReviewServiceImpl implements ShopReviewService {

    private final Logger log = LoggerFactory.getLogger(ShopReviewServiceImpl.class);

    private final ShopReviewRepository shopReviewRepository;

    private final ShopReviewMapper shopReviewMapper;

    private final ShopReviewSearchRepository shopReviewSearchRepository;

    public ShopReviewServiceImpl(
        ShopReviewRepository shopReviewRepository,
        ShopReviewMapper shopReviewMapper,
        ShopReviewSearchRepository shopReviewSearchRepository
    ) {
        this.shopReviewRepository = shopReviewRepository;
        this.shopReviewMapper = shopReviewMapper;
        this.shopReviewSearchRepository = shopReviewSearchRepository;
    }

    @Override
    public Mono<ShopReviewDTO> save(ShopReviewDTO shopReviewDTO) {
        log.debug("Request to save ShopReview : {}", shopReviewDTO);
        return shopReviewRepository
            .save(shopReviewMapper.toEntity(shopReviewDTO))
            .flatMap(shopReviewSearchRepository::save)
            .map(shopReviewMapper::toDto);
    }

    @Override
    public Mono<ShopReviewDTO> partialUpdate(ShopReviewDTO shopReviewDTO) {
        log.debug("Request to partially update ShopReview : {}", shopReviewDTO);

        return shopReviewRepository
            .findById(shopReviewDTO.getId())
            .map(
                existingShopReview -> {
                    shopReviewMapper.partialUpdate(existingShopReview, shopReviewDTO);

                    return existingShopReview;
                }
            )
            .flatMap(shopReviewRepository::save)
            .flatMap(
                savedShopReview -> {
                    shopReviewSearchRepository.save(savedShopReview);

                    return Mono.just(savedShopReview);
                }
            )
            .map(shopReviewMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ShopReviewDTO> findAll() {
        log.debug("Request to get all ShopReviews");
        return shopReviewRepository.findAll().map(shopReviewMapper::toDto);
    }

    public Mono<Long> countAll() {
        return shopReviewRepository.count();
    }

    public Mono<Long> searchCount() {
        return shopReviewSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ShopReviewDTO> findOne(Long id) {
        log.debug("Request to get ShopReview : {}", id);
        return shopReviewRepository.findById(id).map(shopReviewMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ShopReview : {}", id);
        return shopReviewRepository.deleteById(id).then(shopReviewSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ShopReviewDTO> search(String query) {
        log.debug("Request to search ShopReviews for query {}", query);
        return shopReviewSearchRepository.search(query).map(shopReviewMapper::toDto);
    }
}
