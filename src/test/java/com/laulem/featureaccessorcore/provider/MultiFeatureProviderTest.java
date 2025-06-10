package com.laulem.featureaccessorcore.provider;

import dev.openfeature.sdk.ErrorCode;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Value;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultiFeatureProviderTest {
    @Test
    void getBooleanEvaluation_returnsFirstProviderValue() {
        // GIVEN
        InMemoryFlagProvider firstProvider = new InMemoryFlagProvider();
        firstProvider.setFlag("flag", false);
        InMemoryFlagProvider secondProvider = new InMemoryFlagProvider();
        secondProvider.setFlag("flag", true);
        MultiFeatureProvider multiProvider = new MultiFeatureProvider();
        multiProvider.addProvider(firstProvider);
        multiProvider.addProvider(secondProvider);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean firstProviderValue = multiProvider.getBooleanEvaluation("flag", true, evaluationContext).getValue();
        // THEN
        assertFalse(firstProviderValue);
    }

    @Test
    void getBooleanEvaluation_returnsDefaultIfNoProviderHasFlag() {
        // GIVEN
        InMemoryFlagProvider firstProvider = new InMemoryFlagProvider();
        MultiFeatureProvider multiProvider = new MultiFeatureProvider();
        multiProvider.addProvider(firstProvider);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean defaultValue = multiProvider.getBooleanEvaluation("unknown", false, evaluationContext).getValue();
        // THEN
        assertFalse(defaultValue);
    }

    @Test
    void getBooleanEvaluation_returnsDefaultIfNoProviders() {
        // GIVEN
        MultiFeatureProvider multiProvider = new MultiFeatureProvider();
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean defaultValue = multiProvider.getBooleanEvaluation("unknown", false, evaluationContext).getValue();
        // THEN
        assertFalse(defaultValue);
    }

    @Test
    void getStringEvaluation_returnsFirstProviderValue() {
        // GIVEN
        FeatureProvider provider1 = Mockito.mock(FeatureProvider.class);
        FeatureProvider provider2 = Mockito.mock(FeatureProvider.class);
        Mockito.when(provider1.getStringEvaluation(Mockito.eq("flag"), Mockito.anyString(), Mockito.any())).thenReturn(
                dev.openfeature.sdk.ProviderEvaluation.<String>builder().value("first").build());
        Mockito.when(provider2.getStringEvaluation(Mockito.eq("flag"), Mockito.anyString(), Mockito.any())).thenReturn(
                dev.openfeature.sdk.ProviderEvaluation.<String>builder().value("second").build());
        MultiFeatureProvider multiProvider = new MultiFeatureProvider();
        multiProvider.addProvider(provider1);
        multiProvider.addProvider(provider2);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        String value = multiProvider.getStringEvaluation("flag", "default", evaluationContext).getValue();
        // THEN
        assertEquals("first", value);
    }

    @Test
    void getIntegerEvaluation_returnsDefaultIfNoProvider() {
        // GIVEN
        MultiFeatureProvider multiProvider = new MultiFeatureProvider();
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        int value = multiProvider.getIntegerEvaluation("flag", 42, evaluationContext).getValue();
        // THEN
        assertEquals(42, value);
    }

    @Test
    void getDoubleEvaluation_returnsFirstProviderValue() {
        // GIVEN
        FeatureProvider provider1 = Mockito.mock(FeatureProvider.class);
        Mockito.when(provider1.getDoubleEvaluation(Mockito.eq("flag"), Mockito.anyDouble(), Mockito.any())).thenReturn(
                dev.openfeature.sdk.ProviderEvaluation.<Double>builder().value(1.23).build());
        MultiFeatureProvider multiProvider = new MultiFeatureProvider();
        multiProvider.addProvider(provider1);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        double value = multiProvider.getDoubleEvaluation("flag", 0.0, evaluationContext).getValue();
        // THEN
        assertEquals(1.23, value);
    }

    @Test
    void getObjectEvaluation_returnsDefaultIfNoProvider() {
        // GIVEN
        MultiFeatureProvider multiProvider = new MultiFeatureProvider();
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        Value defaultValue = new Value("obj");
        // WHEN
        Value value = multiProvider.getObjectEvaluation("flag", defaultValue, evaluationContext).getValue();
        // THEN
        assertEquals(defaultValue, value);
    }

    @Test
    void getMetadata_returnsCorrectName() {
        // GIVEN
        MultiFeatureProvider multiProvider = new MultiFeatureProvider();
        // WHEN
        String name = multiProvider.getMetadata().getName();
        // THEN
        assertEquals("MultiFeatureProviderImpl", name);
    }

    @Test
    void constructor_withList_addsProviders() {
        // GIVEN
        FeatureProvider provider1 = Mockito.mock(FeatureProvider.class);
        FeatureProvider provider2 = Mockito.mock(FeatureProvider.class);
        List<FeatureProvider> list = List.of(provider1, provider2);
        // WHEN
        MultiFeatureProvider multiProvider = new MultiFeatureProvider(list);
        // THEN
        assertEquals(2, multiProvider.getProviders().size());
    }

    @Test
    void constructor_withProvider_addsProvider() {
        // GIVEN
        FeatureProvider provider = Mockito.mock(FeatureProvider.class);
        // WHEN
        MultiFeatureProvider multiProvider = new MultiFeatureProvider(provider);
        // THEN
        assertEquals(1, multiProvider.getProviders().size());
    }

    @Test
    void getBooleanEvaluation_returnsDefaultIfProviderThrowsException() {
        // GIVEN
        FeatureProvider provider = Mockito.mock(FeatureProvider.class);
        Mockito.when(provider.getBooleanEvaluation(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any())).thenThrow(new RuntimeException("fail"));
        MultiFeatureProvider multiProvider = new MultiFeatureProvider();
        multiProvider.addProvider(provider);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        ProviderEvaluation<Boolean> evaluation = multiProvider.getBooleanEvaluation("flag", true, evaluationContext);
        // THEN
        assertTrue(evaluation.getValue());
        assertEquals(ErrorCode.PARSE_ERROR, evaluation.getErrorCode());
    }

    @Test
    void getBooleanEvaluation_skipsProviderWithErrorCode() {
        // GIVEN
        FeatureProvider provider = Mockito.mock(FeatureProvider.class);
        ProviderEvaluation<Boolean> errorEval = ProviderEvaluation.<Boolean>builder()
                .value(false)
                .errorCode(ErrorCode.PARSE_ERROR)
                .build();
        Mockito.when(provider.getBooleanEvaluation(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any())).thenReturn(errorEval);
        MultiFeatureProvider multiProvider = new MultiFeatureProvider();
        multiProvider.addProvider(provider);
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        boolean value = multiProvider.getBooleanEvaluation("flag", true, evaluationContext).getValue();
        // THEN
        assertTrue(value);
    }
}
