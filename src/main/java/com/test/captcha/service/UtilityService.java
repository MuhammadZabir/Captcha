package com.test.captcha.service;

import com.test.captcha.service.dto.ImageDTO;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

public interface UtilityService {
    /**
     * Get image.
     *
     * @param directory the directory of the image.
     * @return the persisted entity.
     */
    Mono<Resource> getImage(String directory);
}
