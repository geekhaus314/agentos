package com.agentos.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: verifies the Spring application context boots successfully under
 * the test profile (H2 in-memory, Flyway disabled). Run with
 * {@code ./gradlew :core:test}.
 */
@SpringBootTest
@ActiveProfiles("test")
class AgentosApplicationTest {

    @Test
    void contextLoads() {
        // Application context starts without errors.
    }
}
