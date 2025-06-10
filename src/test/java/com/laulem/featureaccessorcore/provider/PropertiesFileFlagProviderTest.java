package com.laulem.featureaccessorcore.provider;

import com.laulem.featureaccessorcore.exception.FeatureFlagLoadException;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.Value;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertiesFileFlagProviderTest {
    @Test
    void getBooleanEvaluation_returnsFlagValueFromPropertiesFile() {
        // GIVEN
        PropertiesFileFlagProvider provider = new PropertiesFileFlagProvider("feature-accessor.properties");
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean newDashboardFlag = provider.getBooleanEvaluation("KNOWN_FLAG", false, evaluationContext).getValue();
        // THEN
        assertTrue(newDashboardFlag);
    }

    @Test
    void getBooleanEvaluation_returnsDefaultIfFlagNotFound() {
        // GIVEN
        PropertiesFileFlagProvider provider = new PropertiesFileFlagProvider("feature-accessor.properties");
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean unknownFlag = provider.getBooleanEvaluation("UNKNOWN_FLAG", false, evaluationContext).getValue();
        // THEN
        assertFalse(unknownFlag);
    }

    @Test
    void getStringEvaluation_returnsDefaultIfFlagNotFound() {
        // GIVEN
        PropertiesFileFlagProvider provider = new PropertiesFileFlagProvider("feature-accessor.properties");
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        String value = provider.getStringEvaluation("UNKNOWN_FLAG", "default", evaluationContext).getValue();
        // THEN
        assertEquals("default", value);
    }

    @Test
    void getIntegerEvaluation_returnsDefaultIfFlagNotFound() {
        // GIVEN
        PropertiesFileFlagProvider provider = new PropertiesFileFlagProvider("feature-accessor.properties");
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        int value = provider.getIntegerEvaluation("UNKNOWN_FLAG", 42, evaluationContext).getValue();
        // THEN
        assertEquals(42, value);
    }

    @Test
    void getDoubleEvaluation_returnsDefaultIfFlagNotFound() {
        // GIVEN
        PropertiesFileFlagProvider provider = new PropertiesFileFlagProvider("feature-accessor.properties");
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        double value = provider.getDoubleEvaluation("UNKNOWN_FLAG", 3.14, evaluationContext).getValue();
        // THEN
        assertEquals(3.14, value);
    }

    @Test
    void getObjectEvaluation_returnsDefaultIfFlagNotFound() {
        // GIVEN
        PropertiesFileFlagProvider provider = new PropertiesFileFlagProvider("feature-accessor.properties");
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        Value defaultValue = new Value("obj");
        Value value = provider.getObjectEvaluation("UNKNOWN_FLAG", defaultValue, evaluationContext).getValue();
        // THEN
        assertEquals(defaultValue, value);
    }

    @Test
    void getMetadata_returnsCorrectName() {
        // GIVEN
        PropertiesFileFlagProvider provider = new PropertiesFileFlagProvider("feature-accessor.properties");
        // WHEN
        String name = provider.getMetadata().getName();
        // THEN
        assertEquals("PropertiesFileFeatureProvider", name);
    }

    @Test
    void getFeatureAccessorPrefix_returnsCorrectPrefix() {
        // GIVEN
        PropertiesFileFlagProvider provider = new PropertiesFileFlagProvider("feature-accessor.properties");
        // WHEN
        String prefix = provider.getFeatureAccessorPrefix();
        // THEN
        assertEquals("FEATURE-ACCESSOR.", prefix);
    }

    @Test
    void constructor_throwsFeatureFlagLoadExceptionIfFileNotFound() {
        // GIVEN
        String fileName = "not-exist.properties";
        // WHEN & THEN
        FeatureFlagLoadException ex = assertThrows(FeatureFlagLoadException.class, () -> new PropertiesFileFlagProvider(fileName));
        assertTrue(ex.getMessage().contains("Failed to load feature flags from not-exist.properties"));
    }
}
