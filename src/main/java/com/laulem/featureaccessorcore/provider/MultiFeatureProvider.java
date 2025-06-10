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

public class MultiFeatureProvider implements FeatureProvider {
    private final List<FeatureProvider> providers = Collections.synchronizedList(new ArrayList<>());

    public MultiFeatureProvider() {
    }

    public MultiFeatureProvider(List<FeatureProvider> providers) {
        this.providers.addAll(providers);
    }

    public MultiFeatureProvider(FeatureProvider provider) {
        providers.add(provider);
    }

    private static <T> boolean providerNotContainError(final ProviderEvaluation<T> x) {
        return x.getErrorCode() == null;
    }

    public void addProvider(FeatureProvider featureProvider) {
        providers.add(featureProvider);
    }

    @Override
    public Metadata getMetadata() {
        return () -> "MultiFeatureProviderImpl";
    }

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

    public List<FeatureProvider> getProviders() {
        return Collections.unmodifiableList(providers);
    }
}
