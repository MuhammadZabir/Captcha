package com.test.captcha.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.test.captcha.domain.Image;
import com.test.captcha.repository.ImageRepository;
import com.test.captcha.repository.search.ImageSearchRepository;
import com.test.captcha.service.ImageService;
import com.test.captcha.service.dto.ImageDTO;
import com.test.captcha.service.mapper.ImageMapper;
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
 * Service Implementation for managing {@link Image}.
 */
@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageRepository imageRepository;

    private final ImageMapper imageMapper;

    private final ImageSearchRepository imageSearchRepository;

    public ImageServiceImpl(ImageRepository imageRepository, ImageMapper imageMapper, ImageSearchRepository imageSearchRepository) {
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
        this.imageSearchRepository = imageSearchRepository;
    }

    @Override
    public Mono<ImageDTO> save(ImageDTO imageDTO) {
        log.debug("Request to save Image : {}", imageDTO);
        return imageRepository.save(imageMapper.toEntity(imageDTO)).flatMap(imageSearchRepository::save).map(imageMapper::toDto);
    }

    @Override
    public Mono<ImageDTO> partialUpdate(ImageDTO imageDTO) {
        log.debug("Request to partially update Image : {}", imageDTO);

        return imageRepository
            .findById(imageDTO.getId())
            .map(
                existingImage -> {
                    imageMapper.partialUpdate(existingImage, imageDTO);

                    return existingImage;
                }
            )
            .flatMap(imageRepository::save)
            .flatMap(
                savedImage -> {
                    imageSearchRepository.save(savedImage);

                    return Mono.just(savedImage);
                }
            )
            .map(imageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ImageDTO> findAll() {
        log.debug("Request to get all Images");
        return imageRepository.findAll().map(imageMapper::toDto);
    }

    public Mono<Long> countAll() {
        return imageRepository.count();
    }

    public Mono<Long> searchCount() {
        return imageSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ImageDTO> findOne(Long id) {
        log.debug("Request to get Image : {}", id);
        return imageRepository.findById(id).map(imageMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Image : {}", id);
        return imageRepository.deleteById(id).then(imageSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ImageDTO> search(String query) {
        log.debug("Request to search Images for query {}", query);
        return imageSearchRepository.search(query).map(imageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ImageDTO> findAllByItem(Long id) {
        log.debug("Request to get all Images by Item ID : {}", id);
        return imageRepository.findByItem(id).map(imageMapper::toDto);
    }
}
