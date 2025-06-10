package com.laulem.featureaccessorcore.provider;

import com.laulem.featureaccessorcore.tool.EvaluationTool;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.Metadata;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Value;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class LambdaFlagProvider implements FeatureProvider {
    private final Map<String, Supplier<Value>> flags = new ConcurrentHashMap<>();

    public LambdaFlagProvider() {
    }

    public LambdaFlagProvider(Map<String, Supplier<Value>> flags) {
        Objects.requireNonNull(flags);
        this.flags.putAll(flags);
    }

    public LambdaFlagProvider(String name, Supplier<Value> flag) {
        setValueFlag(name, flag);
    }

    public void setValueFlag(String key, Supplier<Value> flag) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(flag);
        flags.put(key.toUpperCase(), flag);
    }

    public void setBooleanFlag(String key, Supplier<Boolean> flag) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(flag);
        flags.put(key.toUpperCase(), () -> new Value(flag.get()));
    }

    @Override
    public Metadata getMetadata() {
        return () -> "PropertiesFileFeatureProvider";
    }

    @Override
    public ProviderEvaluation<Boolean> getBooleanEvaluation(String flagKey, Boolean defaultValue, EvaluationContext ctx) {
        return EvaluationTool.evaluateFlagSupplier(flags, flagKey, defaultValue, ctx, x -> x.get().asBoolean());
    }

    @Override
    public ProviderEvaluation<String> getStringEvaluation(String flagKey, String defaultValue, EvaluationContext ctx) {
        return EvaluationTool.evaluateFlagSupplier(flags, flagKey, defaultValue, ctx, x -> x.get().asString());
    }

    @Override
    public ProviderEvaluation<Integer> getIntegerEvaluation(String flagKey, Integer defaultValue, EvaluationContext ctx) {
        return EvaluationTool.evaluateFlagSupplier(flags, flagKey, defaultValue, ctx, x -> x.get().asInteger());
    }

    @Override
    public ProviderEvaluation<Double> getDoubleEvaluation(String flagKey, Double defaultValue, EvaluationContext ctx) {
        return EvaluationTool.evaluateFlagSupplier(flags, flagKey, defaultValue, ctx, x -> x.get().asDouble());
    }

    @Override
    public ProviderEvaluation<Value> getObjectEvaluation(String flagKey, Value defaultValue, EvaluationContext ctx) {
        return EvaluationTool.evaluateFlagSupplier(flags, flagKey, defaultValue, ctx, x -> (Value) x.get().asObject());
    }
}
