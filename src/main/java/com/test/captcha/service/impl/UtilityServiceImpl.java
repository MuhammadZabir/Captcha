package com.test.captcha.service.impl;

import com.test.captcha.domain.Image;
import com.test.captcha.service.UtilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service Implementation for Utility.
 */
@Service
@Transactional
public class UtilityServiceImpl implements UtilityService {
    private final Logger log = LoggerFactory.getLogger(UtilityServiceImpl.class);

    public UtilityServiceImpl() {
    }

    @Override
    public Mono<Resource> getImage(String directory) {
        log.debug("REST request get image : {}", directory);
        Path path = Paths.get(System.getProperty("user.dir") + directory);
        if (!Files.exists(path)) {
            return null;
        }

        Resource resource = new FileSystemResource(path.toFile());
        return Mono.just(resource);
    }
}
