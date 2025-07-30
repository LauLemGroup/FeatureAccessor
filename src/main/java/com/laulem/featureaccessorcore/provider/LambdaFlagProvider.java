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
import java.util.function.Supplier;

/**
 * LambdaFlagProvider is a feature provider that allows setting feature flags using lambda expressions.
 * Flags can be set dynamically at runtime using suppliers.
 */
public class LambdaFlagProvider implements FeatureProvider {
    private final Map<String, Supplier<Value>> flags = new ConcurrentHashMap<>();

    public LambdaFlagProvider() {
    }

    /**
     * Constructs a LambdaFlagProvider with a map of flag suppliers.
     *
     * @param flags the map of flag suppliers
     */
    public LambdaFlagProvider(Map<String, Supplier<Value>> flags) {
        Objects.requireNonNull(flags, "Flags map cannot be null");
        this.flags.putAll(flags);
    }

    /**
     * Constructs a LambdaFlagProvider with a single flag supplier.
     *
     * @param name the flag name
     * @param flag the flag supplier
     */
    public LambdaFlagProvider(String name, Supplier<Value> flag) {
        setValueFlag(name, flag);
    }

    /**
     * Sets a value flag with a supplier.
     *
     * @param key  the flag key
     * @param flag the flag supplier
     */
    public void setValueFlag(String key, Supplier<Value> flag) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(flag, "Flag supplier cannot be null");
        flags.put(key.toUpperCase(), flag);
    }

    /**
     * Sets a boolean flag with a supplier.
     *
     * @param key  the flag key
     * @param flag the boolean flag supplier
     * @param <T>  the type of the flag value. Should be one of Boolean, String, Integer, Double, Number, List, Structure or Value.
     */
    public <T> void setFlag(String key, Supplier<T> flag) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(flag, "Flag supplier cannot be null");
        flags.put(key.toUpperCase(), () -> {
            try {
                return new Value(flag.get());
            } catch (InstantiationException e) {
                throw new ProviderException("Error instantiating flag", e);
            }
        });
    }

    @Override
    public Metadata getMetadata() {
        return () -> "LambdaFlagProvider";
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
