package com.laulem.featureaccessorcore.provider;

import com.laulem.featureaccessorcore.tool.EvaluationTool;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.Metadata;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Value;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryFlagProvider implements FeatureProvider {
    private final Map<String, Value> flags = new ConcurrentHashMap<>();

    public void setFlag(String key, boolean enabled) {
        flags.put(key.toUpperCase(), new Value(enabled));
    }

    @Override
    public Metadata getMetadata() {
        return () -> "PropertiesFileFeatureProvider";
    }

    @Override
    public ProviderEvaluation<Boolean> getBooleanEvaluation(String flagKey, Boolean defaultValue, EvaluationContext ctx) {
        return EvaluationTool.evaluateFlag(flags, flagKey, defaultValue, ctx, Value::asBoolean);
    }

    @Override
    public ProviderEvaluation<String> getStringEvaluation(String flagKey, String defaultValue, EvaluationContext ctx) {
        return EvaluationTool.evaluateFlag(flags, flagKey, defaultValue, ctx, Value::asString);
    }

    @Override
    public ProviderEvaluation<Integer> getIntegerEvaluation(String flagKey, Integer defaultValue, EvaluationContext ctx) {
        return EvaluationTool.evaluateFlag(flags, flagKey, defaultValue, ctx, Value::asInteger);
    }

    @Override
    public ProviderEvaluation<Double> getDoubleEvaluation(String flagKey, Double defaultValue, EvaluationContext ctx) {
        return EvaluationTool.evaluateFlag(flags, flagKey, defaultValue, ctx, Value::asDouble);
    }

    @Override
    public ProviderEvaluation<Value> getObjectEvaluation(String flagKey, Value defaultValue, EvaluationContext ctx) {
        return EvaluationTool.evaluateFlag(flags, flagKey, defaultValue, ctx, v -> v);
    }
}
