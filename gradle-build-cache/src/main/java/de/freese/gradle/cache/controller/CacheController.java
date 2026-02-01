// Created: 11 Apr. 2025
package de.freese.gradle.cache.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import de.freese.gradle.cache.storage.Storage;
import de.freese.gradle.cache.storage.StorageEntry;

/**
 * REST-Controller.
 *
 * @author Thomas Freese
 */
@RestController
@RequestMapping("cache")
public final class CacheController {
    public static final MediaType MEDIA_TYPE_GRADLE_CACHE = new MediaType("application", "vnd.gradle.build-cache-artifact.v2");
    public static final String MEDIA_TYPE_GRADLE_CACHE_VALUE = "application/vnd.gradle.build-cache-artifact.v2";

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheController.class);

    private final Storage storage;

    CacheController(final Storage storage) {
        super();

        this.storage = Objects.requireNonNull(storage, "storage required");
    }

    @GetMapping(value = {"", "/"})
    String home() {
        return "Welcome to HTTP Cache Server - An alternative HTTP based Gradle cache server!";
    }

    /**
     * Jakarta:<br>
     * <pre>{@code
     * public Response test(@PathVariable("id") final UUID id) throws IOException {
     *     return Response.ok((StreamingOutput) outputStream -> {
     *              try (InputStream inputStream = new … {
     *                 inputStream.transferTo(outputStream);
     *                 outputStream.flush();
     *             }
     *         }).build();
     * }
     * // ResponseEntity<byte[]>
     * }</pre>
     *
     * StreamingResponseBody, InputStreamResource working booth alone and with ResponseEntity.<br>
     */
    @GetMapping(value = "/{key}", produces = MEDIA_TYPE_GRADLE_CACHE_VALUE)
    ResponseEntity<StreamingResponseBody> loadCacheEntry(@PathVariable final String key) {
        storage.lock(key);

        try {
            final StorageEntry storageEntry = storage.getStorageEntry(key);

            if (storageEntry == null) {
                LOGGER.info("No cache available for key: {}", key);

                return ResponseEntity.notFound().build();
            }

            LOGGER.info("Returning cache for key: {}", key);

            return ResponseEntity.ok()
                    .contentType(MEDIA_TYPE_GRADLE_CACHE)
                    // .contentType(MediaType.parseMediaType(MEDIA_TYPE_GRADLE_CACHE_VALUE))
                    .header(HttpHeaders.CONTENT_LENGTH, Long.toString(storageEntry.getContentLength()))
                    .body(outputStream -> {
                        storageEntry.transferTo(outputStream);

                        outputStream.flush();
                    });
        }
        finally {
            storage.unlock(key);
        }
    }

    /**
     * Jakarta:<br>
     * <pre>{@code
     * public void test(@PathVariable("id") final UUID id, final InputStream inputStream) throws IOException {}
     * }</pre>
     * <br>
     *
     * AtRequestBody final byte[] payload
     */
    @PutMapping(value = "/{key}", consumes = MEDIA_TYPE_GRADLE_CACHE_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    void storeCacheEntry(@PathVariable final String key, @RequestBody final InputStreamResource inputStreamResource) throws IOException {
        LOGGER.info("Storing key: {}", key);

        storage.lock(key);

        // PUT requests may also return a 413 Payload Too Large response to indicate that the payload is larger than can be accepted.
        try (InputStream inputStream = inputStreamResource.getInputStream()) {
            storage.put(key, inputStream);
        }
        finally {
            storage.unlock(key);
        }
    }
}
