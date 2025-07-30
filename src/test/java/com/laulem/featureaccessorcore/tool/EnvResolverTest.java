package com.laulem.featureaccessorcore.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EnvResolverTest {
    @Test
    void resolveEnvVars_returnsEnvValueIfPresent() {
        // GIVEN
        String key = "PWD";
        String value = System.getenv(key);
        // WHEN
        String result = EnvResolver.resolveEnvVars("${PWD}");
        // THEN
        assertEquals(value, result);
    }

    @Test
    void resolveEnvVars_returnsDefaultIfEnvNotPresent() {
        String result = EnvResolver.resolveEnvVars("${ENV_RESOLVER_TEST:defaultValue}");
        assertEquals("defaultValue", result);
    }

    @Test
    void resolveEnvVars_returnsEmptyIfNoEnvAndNoDefault() {
        String result = EnvResolver.resolveEnvVars("${ENV_RESOLVER_TEST}");
        assertEquals("", result);
    }

    @Test
    void resolveEnvVars_returnsInputIfNoPattern() {
        assertEquals("no pattern", EnvResolver.resolveEnvVars("no pattern"));
    }

    @Test
    void resolveEnvVars_returnsNullIfInputIsNull() {
        // WHEN
        String result = EnvResolver.resolveEnvVars(null);
        // THEN
        assertNull(result);
    }
}
