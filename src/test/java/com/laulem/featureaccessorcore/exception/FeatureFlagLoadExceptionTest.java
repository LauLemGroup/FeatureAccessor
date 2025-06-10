package com.laulem.featureaccessorcore.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeatureFlagLoadExceptionTest {
    @Test
    void constructor_setsMessageAndCause() {
        Throwable cause = new RuntimeException("cause");
        FeatureFlagLoadException ex = new FeatureFlagLoadException("error", cause);
        assertEquals("error", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}

