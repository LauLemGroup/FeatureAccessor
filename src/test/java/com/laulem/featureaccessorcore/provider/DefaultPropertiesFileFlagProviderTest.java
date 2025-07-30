package com.laulem.featureaccessorcore.provider;

import dev.openfeature.sdk.EvaluationContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultPropertiesFileFlagProviderTest {
    @Test
    void getBooleanEvaluation_returnsFlagValueFromDefaultFile() {
        // GIVEN
        DefaultPropertiesFileFlagProvider provider = new DefaultPropertiesFileFlagProvider("feature-accessor.properties");
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean flagValue = provider.getBooleanEvaluation("KNOWN_FLAG", false, evaluationContext).getValue();
        // THEN
        assertTrue(flagValue);
    }

    @Test
    void getBooleanEvaluation_returnsDefaultIfFlagNotFound() {
        // GIVEN
        DefaultPropertiesFileFlagProvider provider = new DefaultPropertiesFileFlagProvider("feature-accessor.properties");
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean unknownFlag = provider.getBooleanEvaluation("UNKNOWN_FLAG", false, evaluationContext).getValue();
        // THEN
        assertFalse(unknownFlag);
    }

    @Test
    void constructor_usesSystemPropertyIfPresent() {
        // GIVEN
        System.setProperty("feature-accessor.properties-file", "feature-accessor.properties");
        // WHEN
        DefaultPropertiesFileFlagProvider provider = new DefaultPropertiesFileFlagProvider();
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        boolean newDashboardFlag = provider.getBooleanEvaluation("known_flag", false, evaluationContext).getValue();
        // THEN
        assertTrue(newDashboardFlag);
        System.clearProperty("feature-accessor.properties-file");
    }

    @Test
    void constructor_usesDefaultIfNoSystemProperty() {
        // GIVEN
        System.clearProperty("feature-accessor.properties-file");
        // WHEN
        DefaultPropertiesFileFlagProvider provider = new DefaultPropertiesFileFlagProvider();
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        boolean flagValue = provider.getBooleanEvaluation("known_flag", false, evaluationContext).getValue();
        // THEN
        assertTrue(flagValue);
    }

    @Test
    void constructor_throwsExceptionIfFileNotFound() {
        // GIVEN
        String fileName = "not-exist.properties";
        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> new DefaultPropertiesFileFlagProvider(fileName));
    }
}
