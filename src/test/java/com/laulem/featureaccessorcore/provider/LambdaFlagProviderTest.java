package com.laulem.featureaccessorcore.provider;

import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LambdaFlagProviderTest {
    private LambdaFlagProvider provider;

    @BeforeEach
    void setUp() {
        provider = new LambdaFlagProvider();
    }

    @Test
    void setBooleanFlag_and_getBooleanEvaluation() {
        // GIVEN
        provider.setBooleanFlag("dynamic_flag", () -> true);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean dynamicFlagValue = provider.getBooleanEvaluation("dynamic_flag", false, evaluationContext).getValue();
        // THEN
        assertTrue(dynamicFlagValue);
    }

    @Test
    void getBooleanEvaluation_returnsDefaultIfNotSet() {
        // GIVEN
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean defaultValue = provider.getBooleanEvaluation("unknown", false, evaluationContext).getValue();
        // THEN
        assertFalse(defaultValue);
    }

    @Test
    void constructor_withMap_setsFlags() {
        // GIVEN
        Map<String, Supplier<Value>> map = new java.util.HashMap<>();
        map.put("FLAG", () -> new Value("ok"));

        LambdaFlagProvider localProvider = new LambdaFlagProvider(map);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        String value = localProvider.getStringEvaluation("FLAG", "fail", evaluationContext).getValue();
        // THEN
        assertEquals("ok", value);
    }

    @Test
    void constructor_withNameAndSupplier_setsFlag() {
        // GIVEN
        LambdaFlagProvider localProvider = new LambdaFlagProvider("FLAG", () -> new Value(123));
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        int value = localProvider.getIntegerEvaluation("FLAG", 0, evaluationContext).getValue();
        // THEN
        assertEquals(123, value);
    }

    @Test
    void setValueFlag_nullKey_throwsException() {
        // GIVEN
        LambdaFlagProvider localProvider = new LambdaFlagProvider();
        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> localProvider.setValueFlag(null, () -> new Value(true)));
    }

    @Test
    void setValueFlag_nullFlag_throwsException() {
        // GIVEN
        LambdaFlagProvider localProvider = new LambdaFlagProvider();
        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> localProvider.setValueFlag("FLAG", null));
    }

    @Test
    void setBooleanFlag_nullKey_throwsException() {
        // GIVEN
        LambdaFlagProvider localProvider = new LambdaFlagProvider();
        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> localProvider.setBooleanFlag(null, () -> true));
    }

    @Test
    void setBooleanFlag_nullFlag_throwsException() {
        // GIVEN
        LambdaFlagProvider localProvider = new LambdaFlagProvider();
        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> localProvider.setBooleanFlag("FLAG", null));
    }

    @Test
    void getStringEvaluation_returnsDefaultIfNotSet() {
        // GIVEN
        LambdaFlagProvider localProvider = new LambdaFlagProvider();
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        String value = localProvider.getStringEvaluation("unknown", "default", evaluationContext).getValue();
        // THEN
        assertEquals("default", value);
    }

    @Test
    void getIntegerEvaluation_returnsDefaultIfNotSet() {
        // GIVEN
        LambdaFlagProvider localProvider = new LambdaFlagProvider();
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        int value = localProvider.getIntegerEvaluation("unknown", 42, evaluationContext).getValue();
        // THEN
        assertEquals(42, value);
    }

    @Test
    void getDoubleEvaluation_returnsDefaultIfNotSet() {
        // GIVEN
        LambdaFlagProvider localProvider = new LambdaFlagProvider();
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        double value = localProvider.getDoubleEvaluation("unknown", 3.14, evaluationContext).getValue();
        // THEN
        assertEquals(3.14, value);
    }

    @Test
    void getObjectEvaluation_returnsDefaultIfNotSet() {
        // GIVEN
        LambdaFlagProvider localProvider = new LambdaFlagProvider();
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        Value defaultValue = new Value("obj");
        Value value = localProvider.getObjectEvaluation("unknown", defaultValue, evaluationContext).getValue();
        // THEN
        assertEquals(defaultValue, value);
    }

    @Test
    void getMetadata_returnsCorrectName() {
        // GIVEN
        LambdaFlagProvider localProvider = new LambdaFlagProvider();
        // WHEN
        String name = localProvider.getMetadata().getName();
        // THEN
        assertEquals("PropertiesFileFeatureProvider", name);
    }
}
