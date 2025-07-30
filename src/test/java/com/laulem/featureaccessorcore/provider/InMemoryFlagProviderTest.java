package com.laulem.featureaccessorcore.provider;

import com.laulem.featureaccessorcore.exception.ProviderException;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryFlagProviderTest {
    private InMemoryFlagProvider provider;

    @BeforeEach
    void setUp() {
        provider = new InMemoryFlagProvider();
    }

    @Test
    void setFlag_and_getBooleanEvaluation() {
        // GIVEN
        provider.setFlag("test_flag", true);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean lowerCaseFlagValue = provider.getBooleanEvaluation("test_flag", false, evaluationContext).getValue();
        boolean upperCaseFlagValue = provider.getBooleanEvaluation("TEST_FLAG", false, evaluationContext).getValue();
        // THEN
        assertTrue(lowerCaseFlagValue);
        assertTrue(upperCaseFlagValue);
    }

    @Test
    void getBooleanEvaluation_returnsDefaultIfNotSet() {
        // GIVEN
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean result = provider.getBooleanEvaluation("unknown", false, evaluationContext).getValue();
        // THEN
        assertFalse(result);
    }

    @Test
    void getStringEvaluation_returnsDefaultIfNotSet() {
        // GIVEN
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        String result = provider.getStringEvaluation("unknown", "default", evaluationContext).getValue();
        // THEN
        assertEquals("default", result);
    }

    @Test
    void getIntegerEvaluation_returnsDefaultIfNotSet() {
        // GIVEN
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        int result = provider.getIntegerEvaluation("unknown", 42, evaluationContext).getValue();
        // THEN
        assertEquals(42, result);
    }

    @Test
    void getDoubleEvaluation_returnsDefaultIfNotSet() {
        // GIVEN
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        double result = provider.getDoubleEvaluation("unknown", 3.14, evaluationContext).getValue();
        // THEN
        assertEquals(3.14, result);
    }

    @Test
    void getObjectEvaluation_returnsDefaultIfNotSet() {
        // GIVEN
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        Value defaultValue = new Value("obj");
        // WHEN
        Value result = provider.getObjectEvaluation("unknown", defaultValue, evaluationContext).getValue();
        // THEN
        assertEquals(defaultValue, result);
    }

    @Test
    void setFlag_isCaseInsensitive() {
        // GIVEN
        provider.setFlag("myFlag", true);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean value = provider.getBooleanEvaluation("MYFLAG", false, evaluationContext).getValue();
        // THEN
        assertTrue(value);
    }

    @Test
    void getStringEvaluation_returnsSetValue() {
        // GIVEN
        provider.setFlag("string_flag", "hello");
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        String result = provider.getStringEvaluation("string_flag", "default", evaluationContext).getValue();
        // THEN
        assertEquals("hello", result);
    }

    @Test
    void getBooleanEvaluation_returnsSetValue() {
        // GIVEN
        provider.setFlag("bool_flag", true);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean result = provider.getBooleanEvaluation("bool_flag", false, evaluationContext).getValue();
        // THEN
        assertTrue(result);
    }

    @Test
    void getIntegerEvaluation_returnsSetValue() {
        // GIVEN
        provider.setFlag("int_flag", 123);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        int result = provider.getIntegerEvaluation("int_flag", 0, evaluationContext).getValue();
        // THEN
        assertEquals(123, result);
    }

    @Test
    void getDoubleEvaluation_returnsSetValue() {
        // GIVEN
        provider.setFlag("double_flag", 2.71);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        double result = provider.getDoubleEvaluation("double_flag", 0.0, evaluationContext).getValue();
        // THEN
        assertEquals(2.71, result);
    }

    @Test
    void setFlag_overwritesPreviousValue() {
        // GIVEN
        provider.setFlag("overwrite_flag", false);
        provider.setFlag("overwrite_flag", true);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean result = provider.getBooleanEvaluation("overwrite_flag", false, evaluationContext).getValue();
        // THEN
        assertTrue(result);
    }

    @Test
    void getMetadata_returnsCorrectName() {
        // GIVEN
        // WHEN
        String name = provider.getMetadata().getName();
        // THEN
        assertEquals("InMemoryFlagProvider", name);
    }

    @Test
    void setFlag_withObjectType_shouldThrowException() {
        // GIVEN
        Object customObject = new Object();
        // WHEN & THEN
        assertThrows(ProviderException.class, () -> provider.setFlag("object_flag", customObject));
    }
}
