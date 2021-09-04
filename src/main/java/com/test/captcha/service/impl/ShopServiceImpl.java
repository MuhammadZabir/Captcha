package com.test.captcha.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.captcha.domain.Shop;
import com.test.captcha.repository.ShopRepository;
import com.test.captcha.repository.search.ShopSearchRepository;
import com.test.captcha.service.ShopService;
import com.test.captcha.service.dto.ShopDTO;
import com.test.captcha.service.mapper.ShopMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Shop}.
 */
@Service
@Transactional
public class ShopServiceImpl implements ShopService {

    private final Logger log = LoggerFactory.getLogger(ShopServiceImpl.class);

    private final ShopRepository shopRepository;

    private final ShopMapper shopMapper;

    private final ShopSearchRepository shopSearchRepository;

    public ShopServiceImpl(ShopRepository shopRepository, ShopMapper shopMapper, ShopSearchRepository shopSearchRepository) {
        this.shopRepository = shopRepository;
        this.shopMapper = shopMapper;
        this.shopSearchRepository = shopSearchRepository;
    }

    @Override
    public Mono<ShopDTO> save(ShopDTO shopDTO) {
        log.debug("Request to save Shop : {}", shopDTO);
        return shopRepository.save(shopMapper.toEntity(shopDTO)).flatMap(shopSearchRepository::save).map(shopMapper::toDto);
    }

    @Override
    public Mono<ShopDTO> partialUpdate(ShopDTO shopDTO) {
        log.debug("Request to partially update Shop : {}", shopDTO);

        return shopRepository
            .findById(shopDTO.getId())
            .map(
                existingShop -> {
                    shopMapper.partialUpdate(existingShop, shopDTO);

                    return existingShop;
                }
            )
            .flatMap(shopRepository::save)
            .flatMap(
                savedShop -> {
                    shopSearchRepository.save(savedShop);

                    return Mono.just(savedShop);
                }
            )
            .map(shopMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ShopDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Shops");
        return shopRepository.findAllBy(pageable).map(shopMapper::toDto);
    }

    public Mono<Long> countAll() {
        return shopRepository.count();
    }

    public Mono<Long> searchCount() {
        return shopSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ShopDTO> findOne(Long id) {
        log.debug("Request to get Shop : {}", id);
        return shopRepository.findById(id).map(shopMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Shop : {}", id);
        return shopRepository.deleteById(id).then(shopSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ShopDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Shops for query {}", query);
        return shopSearchRepository.search(query, pageable).map(shopMapper::toDto);
    }
}
