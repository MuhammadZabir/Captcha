package com.test.captcha.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.captcha.domain.Item;
import com.test.captcha.repository.ItemRepository;
import com.test.captcha.repository.search.ItemSearchRepository;
import com.test.captcha.service.ItemService;
import com.test.captcha.service.dto.ItemDTO;
import com.test.captcha.service.mapper.ItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Item}.
 */
@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    private final ItemSearchRepository itemSearchRepository;

    public ItemServiceImpl(ItemRepository itemRepository, ItemMapper itemMapper, ItemSearchRepository itemSearchRepository) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.itemSearchRepository = itemSearchRepository;
    }

    @Override
    public Mono<ItemDTO> save(ItemDTO itemDTO) {
        log.debug("Request to save Item : {}", itemDTO);
        return itemRepository.save(itemMapper.toEntity(itemDTO)).flatMap(itemSearchRepository::save).map(itemMapper::toDto);
    }

    @Override
    public Mono<ItemDTO> partialUpdate(ItemDTO itemDTO) {
        log.debug("Request to partially update Item : {}", itemDTO);

        return itemRepository
            .findById(itemDTO.getId())
            .map(
                existingItem -> {
                    itemMapper.partialUpdate(existingItem, itemDTO);

                    return existingItem;
                }
            )
            .flatMap(itemRepository::save)
            .flatMap(
                savedItem -> {
                    itemSearchRepository.save(savedItem);

                    return Mono.just(savedItem);
                }
            )
            .map(itemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Items");
        return itemRepository.findAllBy(pageable).map(itemMapper::toDto);
    }

    public Mono<Long> countAll() {
        return itemRepository.count();
    }

    public Mono<Long> searchCount() {
        return itemSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemDTO> findOne(Long id) {
        log.debug("Request to get Item : {}", id);
        return itemRepository.findById(id).map(itemMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Item : {}", id);
        return itemRepository.deleteById(id).then(itemSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Items for query {}", query);
        return itemSearchRepository.search(query, pageable).map(itemMapper::toDto);
    }
}
