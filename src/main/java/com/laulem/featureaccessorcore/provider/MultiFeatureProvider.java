package com.laulem.featureaccessorcore.provider;

import dev.openfeature.sdk.ErrorCode;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.Metadata;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * MultiFeatureProvider is a feature provider that aggregates multiple feature providers.
 * It evaluates flags using the first successful provider and returns the result.
 */
public class MultiFeatureProvider implements FeatureProvider {
    private final List<FeatureProvider> providers = Collections.synchronizedList(new ArrayList<>());

    public MultiFeatureProvider() {
    }

    /**
     * Constructs a MultiFeatureProvider with a list of feature providers.
     *
     * @param providers the list of feature providers
     */
    public MultiFeatureProvider(List<FeatureProvider> providers) {
        this.providers.addAll(providers);
    }

    /**
     * Constructs a MultiFeatureProvider with a single feature provider.
     *
     * @param featureProvider the feature provider to add
     */
    public MultiFeatureProvider(FeatureProvider featureProvider) {
        Objects.requireNonNull(featureProvider, "Feature provider cannot be null");
        providers.add(featureProvider);
    }

    /**
     * Checks if the provider evaluation does not contain an error.
     *
     * @param x the provider evaluation
     * @param <T> the type of the evaluation value
     * @return true if there is no error code, false otherwise
     */
    private static <T> boolean providerNotContainError(final ProviderEvaluation<T> x) {
        return x.getErrorCode() == null;
    }

    /**
     * Adds a feature provider to the list.
     *
     * @param featureProvider the feature provider to add
     */
    public void addProvider(FeatureProvider featureProvider) {
        Objects.requireNonNull(featureProvider, "Feature provider cannot be null");
        providers.add(featureProvider);
    }

    @Override
    public Metadata getMetadata() {
        return () -> "MultiFeatureProviderImpl";
    }

    /**
     * Evaluates a flag using all providers and returns the first successful evaluation.
     *
     * @param defaultValue the default value to return if no provider returns a value without error
     * @param ctx the evaluation context (not used)
     * @param extractor function to extract the ProviderEvaluation from a FeatureProvider
     * @param <T> the type of the flag value
     * @return the ProviderEvaluation result
     */
    private <T> ProviderEvaluation<T> evaluateFlag(T defaultValue, EvaluationContext ctx, Function<FeatureProvider, ProviderEvaluation<T>> extractor) {
        try {
            Optional<ProviderEvaluation<T>> evaluation = providers.stream()
                    .map(extractor)
                    .filter(Objects::nonNull)
                    .filter(MultiFeatureProvider::providerNotContainError)
                    .findFirst();

            return evaluation.orElseGet(() -> ProviderEvaluation.<T>builder()
                    .reason("DEFAULT")
                    .value(defaultValue)
                    .errorCode(ErrorCode.FLAG_NOT_FOUND)
                    .build());

        } catch (Exception e) {
            return ProviderEvaluation.<T>builder()
                    .reason("ERROR")
                    .value(defaultValue)
                    .errorCode(ErrorCode.PARSE_ERROR)
                    .build();
        }
    }

    @Override
    public ProviderEvaluation<Boolean> getBooleanEvaluation(String flagKey, Boolean defaultValue, EvaluationContext ctx) {
        return evaluateFlag(defaultValue, ctx, x -> x.getBooleanEvaluation(flagKey, defaultValue, ctx));
    }

    @Override
    public ProviderEvaluation<String> getStringEvaluation(String flagKey, String defaultValue, EvaluationContext ctx) {
        return evaluateFlag(defaultValue, ctx, x -> x.getStringEvaluation(flagKey, defaultValue, ctx));
    }

    @Override
    public ProviderEvaluation<Integer> getIntegerEvaluation(String flagKey, Integer defaultValue, EvaluationContext ctx) {
        return evaluateFlag(defaultValue, ctx, x -> x.getIntegerEvaluation(flagKey, defaultValue, ctx));
    }

    @Override
    public ProviderEvaluation<Double> getDoubleEvaluation(String flagKey, Double defaultValue, EvaluationContext ctx) {
        return evaluateFlag(defaultValue, ctx, x -> x.getDoubleEvaluation(flagKey, defaultValue, ctx));
    }

    @Override
    public ProviderEvaluation<Value> getObjectEvaluation(String flagKey, Value defaultValue, EvaluationContext ctx) {
        return evaluateFlag(defaultValue, ctx, x -> x.getObjectEvaluation(flagKey, defaultValue, ctx));
    }

    /**
     * Returns an unmodifiable list of feature providers.
     *
     * @return the list of feature providers
     */
    public List<FeatureProvider> getProviders() {
        return Collections.unmodifiableList(providers);
    }
}
