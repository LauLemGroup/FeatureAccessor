package com.laulem.featureaccessorcore.provider;

import com.laulem.featureaccessorcore.exception.ProviderException;
import com.laulem.featureaccessorcore.tool.EvaluationTool;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.Metadata;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Value;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InMemoryFlagProvider is a feature provider that stores feature flags in memory.
 * It allows setting and retrieving flags dynamically at runtime.
 */
public class InMemoryFlagProvider implements FeatureProvider {
    private final Map<String, Value> flags = new ConcurrentHashMap<>();

    /**
     * Sets a flag in memory with the specified key and value.
     *
     * @param key     the flag key
     * @param enabled the flag value
     * @param <T>     the type of the flag value. Should be one of Boolean, String, Integer, Double, Number, List, Structure or Value.
     */
    public <T> void setFlag(String key, T enabled) {
        try {
            Objects.requireNonNull(key, "Key cannot be null");
            Objects.requireNonNull(enabled, "Enabled cannot be null");
            flags.put(key.toUpperCase(), new Value(enabled));
        } catch (InstantiationException e) {
            throw new ProviderException("Erreur lors de l'instanciation du flag", e);
        }
    }

    @Override
    public Metadata getMetadata() {
        return () -> "InMemoryFlagProvider";
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
