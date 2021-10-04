package com.test.captcha.web.rest;

import com.test.captcha.service.UtilityService;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.reactive.ResponseUtil;

import java.net.URISyntaxException;

/**
 * REST controller for managing Utility.
 */
@RestController
@RequestMapping("/api")
public class UtilityResource {

    private final Logger log = LoggerFactory.getLogger(UtilityResource.class);

    private UtilityService utilityService;

    public UtilityResource(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    /**
     * {@code POST  /utility/get-image} : Get Image File.
     *
     * @param directory the directory of the image file.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with the Resource hold the file, or with status {@code 400 (Bad Request)} if the image has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/utility/get-image")
    @Timed
    public Mono<ResponseEntity<Resource>> getImage(@RequestBody String directory) throws URISyntaxException {
        log.debug("REST request get image : {}", directory);
        Mono<Resource> resourceMono = utilityService.getImage(directory);
        if (resourceMono == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.NO_CONTENT));
        }

        return ResponseUtil.wrapOrNotFound(resourceMono);
    }
}
