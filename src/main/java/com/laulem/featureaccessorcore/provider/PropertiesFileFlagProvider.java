package com.laulem.featureaccessorcore.provider;

import com.laulem.featureaccessorcore.exception.FeatureFlagLoadException;
import com.laulem.featureaccessorcore.tool.EnvResolver;
import com.laulem.featureaccessorcore.tool.EvaluationTool;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.Metadata;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Value;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesFileFlagProvider implements FeatureProvider {
    private static final String FEATURE_ACCESSOR = "FEATURE-ACCESSOR";
    private static final String FEATURE_ACCESSOR_PREFIX = FEATURE_ACCESSOR + ".";

    private final Map<String, Value> flags = new ConcurrentHashMap<>();

    public PropertiesFileFlagProvider(String fileName) {
        Objects.requireNonNull(fileName);
        loadProperties(fileName);
    }

    private void loadProperties(String fileName) {
        try (var input = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IllegalArgumentException("Properties file not found: " + fileName);
            }
            flags.clear();

            Properties properties = new Properties();
            properties.load(input);

            String featureAccessorPrefix = getFeatureAccessorPrefix();
            int featureAccessorPrefixLength = featureAccessorPrefix.length();

            for (String key : properties.stringPropertyNames()) {
                if (key.toUpperCase().startsWith(featureAccessorPrefix)) {
                    String flagName = key.toUpperCase().substring(featureAccessorPrefixLength);
                    flags.put(flagName, new Value(Boolean.parseBoolean(EnvResolver.resolveEnvVars(properties.getProperty(key)))));
                }
            }
        } catch (Exception e) {
            throw new FeatureFlagLoadException("Failed to load feature flags from " + fileName, e);
        }
    }

    public String getFeatureAccessorPrefix() {
        return FEATURE_ACCESSOR_PREFIX;
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
