package com.laulem.featureaccessorcore.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvResolverTest {
    @Test
    void resolveEnvVars_returnsEnvValueIfPresent() {
        String key = "PWD";
        String value = System.getenv(key);
        assertEquals(value, EnvResolver.resolveEnvVars("${PWD}"));
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
}

