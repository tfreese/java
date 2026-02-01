// Created: 11 Apr. 2025
package de.freese.gradle.cache;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import jakarta.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import de.freese.gradle.cache.controller.CacheController;

/**
 * @author Thomas Freese
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("memory")
class TestBuildCache {
    // @TempDir(cleanup = CleanupMode.ALWAYS)
    // private static Path pathTest;

    @Resource
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void testMiss() throws Exception {
        final String key = "someKey";

        mockMvc.perform(get("/cache/" + key)
                        .accept(CacheController.MEDIA_TYPE_GRADLE_CACHE)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void testStorageAndRetrieve() throws Exception {
        final String key = getKey();
        final byte[] payload = getKey().getBytes(StandardCharsets.UTF_8);

        mockMvc.perform(put("/cache/" + key)
                        .contentType(CacheController.MEDIA_TYPE_GRADLE_CACHE)
                        .content(payload)
                )
                .andExpect(status().isAccepted());

        mockMvc.perform(get("/cache/" + key)
                        .accept(CacheController.MEDIA_TYPE_GRADLE_CACHE)
                )
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_LENGTH, Integer.toString(payload.length)))
                .andExpect(content().contentType(CacheController.MEDIA_TYPE_GRADLE_CACHE))
                .andDo(result -> {
                    assertArrayEquals(payload, result.getResponse().getContentAsByteArray());
                });
    }

    private String getKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
