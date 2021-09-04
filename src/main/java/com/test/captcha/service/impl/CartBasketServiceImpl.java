package com.test.captcha.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.captcha.domain.CartBasket;
import com.test.captcha.repository.CartBasketRepository;
import com.test.captcha.repository.search.CartBasketSearchRepository;
import com.test.captcha.service.CartBasketService;
import com.test.captcha.service.dto.CartBasketDTO;
import com.test.captcha.service.mapper.CartBasketMapper;
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
 * Service Implementation for managing {@link CartBasket}.
 */
@Service
@Transactional
public class CartBasketServiceImpl implements CartBasketService {

    private final Logger log = LoggerFactory.getLogger(CartBasketServiceImpl.class);

    private final CartBasketRepository cartBasketRepository;

    private final CartBasketMapper cartBasketMapper;

    private final CartBasketSearchRepository cartBasketSearchRepository;

    public CartBasketServiceImpl(
        CartBasketRepository cartBasketRepository,
        CartBasketMapper cartBasketMapper,
        CartBasketSearchRepository cartBasketSearchRepository
    ) {
        this.cartBasketRepository = cartBasketRepository;
        this.cartBasketMapper = cartBasketMapper;
        this.cartBasketSearchRepository = cartBasketSearchRepository;
    }

    @Override
    public Mono<CartBasketDTO> save(CartBasketDTO cartBasketDTO) {
        log.debug("Request to save CartBasket : {}", cartBasketDTO);
        return cartBasketRepository
            .save(cartBasketMapper.toEntity(cartBasketDTO))
            .flatMap(cartBasketSearchRepository::save)
            .map(cartBasketMapper::toDto);
    }

    @Override
    public Mono<CartBasketDTO> partialUpdate(CartBasketDTO cartBasketDTO) {
        log.debug("Request to partially update CartBasket : {}", cartBasketDTO);

        return cartBasketRepository
            .findById(cartBasketDTO.getId())
            .map(
                existingCartBasket -> {
                    cartBasketMapper.partialUpdate(existingCartBasket, cartBasketDTO);

                    return existingCartBasket;
                }
            )
            .flatMap(cartBasketRepository::save)
            .flatMap(
                savedCartBasket -> {
                    cartBasketSearchRepository.save(savedCartBasket);

                    return Mono.just(savedCartBasket);
                }
            )
            .map(cartBasketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CartBasketDTO> findAll() {
        log.debug("Request to get all CartBaskets");
        return cartBasketRepository.findAll().map(cartBasketMapper::toDto);
    }

    public Mono<Long> countAll() {
        return cartBasketRepository.count();
    }

    public Mono<Long> searchCount() {
        return cartBasketSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CartBasketDTO> findOne(Long id) {
        log.debug("Request to get CartBasket : {}", id);
        return cartBasketRepository.findById(id).map(cartBasketMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete CartBasket : {}", id);
        return cartBasketRepository.deleteById(id).then(cartBasketSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CartBasketDTO> search(String query) {
        log.debug("Request to search CartBaskets for query {}", query);
        return cartBasketSearchRepository.search(query).map(cartBasketMapper::toDto);
    }
}
