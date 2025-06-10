package com.laulem.featureaccessorcore.tool;

import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Value;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvaluationToolTest {
    @Test
    void evaluateFlag_returnsStaticReasonIfFlagPresent() {
        // GIVEN
        Map<String, Value> flagMap = new HashMap<>();
        flagMap.put("FLAG", new Value(true));
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        ProviderEvaluation<Boolean> evaluation = EvaluationTool.evaluateFlag(flagMap, "FLAG", false, evaluationContext, Value::asBoolean);
        // THEN
        assertTrue(evaluation.getValue());
        assertEquals(EvaluationTool.STATIC_REASON, evaluation.getReason());
        assertNull(evaluation.getErrorCode());
    }

    @Test
    void evaluateFlag_returnsDefaultReasonIfFlagMissing() {
        // GIVEN
        Map<String, Value> flagMap = new HashMap<>();
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        ProviderEvaluation<Boolean> evaluation = EvaluationTool.evaluateFlag(flagMap, "UNKNOWN", false, evaluationContext, Value::asBoolean);
        // THEN
        assertFalse(evaluation.getValue());
        assertEquals(EvaluationTool.DEFAULT_REASON, evaluation.getReason());
        assertNotNull(evaluation.getErrorCode());
    }

    @Test
    void evaluateFlagSupplier_returnsStaticReasonIfFlagPresent() {
        // GIVEN
        Map<String, Supplier<Value>> flagSupplierMap = new HashMap<>();
        flagSupplierMap.put("FLAG", () -> new Value(true));
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        ProviderEvaluation<Boolean> evaluation = EvaluationTool.evaluateFlagSupplier(flagSupplierMap, "FLAG", false, evaluationContext, s -> s.get().asBoolean());
        // THEN
        assertTrue(evaluation.getValue());
        assertEquals(EvaluationTool.STATIC_REASON, evaluation.getReason());
        assertNull(evaluation.getErrorCode());
    }

    @Test
    void evaluateFlagSupplier_returnsDefaultReasonIfFlagMissing() {
        // GIVEN
        Map<String, Supplier<Value>> flagSupplierMap = new HashMap<>();
        EvaluationContext evaluationContext = Mockito.mock(EvaluationContext.class);
        // WHEN
        ProviderEvaluation<Boolean> evaluation = EvaluationTool.evaluateFlagSupplier(flagSupplierMap, "UNKNOWN", false, evaluationContext, s -> s.get().asBoolean());
        // THEN
        assertFalse(evaluation.getValue());
        assertEquals(EvaluationTool.DEFAULT_REASON, evaluation.getReason());
        assertNotNull(evaluation.getErrorCode());
    }
}
