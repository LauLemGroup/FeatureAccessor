package com.laulem.featureaccessorcore.provider;

import com.laulem.featureaccessorcore.exception.ProviderException;
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

/**
 * PropertiesFileFlagProvider is a feature provider that loads feature flags from a properties file.
 * It allows dynamic evaluation of flags based on the properties defined in the file.
 * This provider uses the "FEATURE-ACCESSOR" prefix to identify feature flags in the properties file.
 */
public class PropertiesFileFlagProvider implements FeatureProvider {
    private static final String FEATURE_ACCESSOR = "FEATURE-ACCESSOR";
    private static final String FEATURE_ACCESSOR_PREFIX = FEATURE_ACCESSOR + ".";

    private final Map<String, Value> flags = new ConcurrentHashMap<>();

    /**
     * Constructs a PropertiesFileFlagProvider and loads feature flags from the specified file.
     *
     * @param fileName the name of the properties file
     */
    public PropertiesFileFlagProvider(String fileName) {
        Objects.requireNonNull(fileName, "File name cannot be null");
        loadProperties(fileName);
    }

    /**
     * Loads feature flags from the specified properties file.
     *
     * @param fileName the name of the properties file
     */
    private void loadProperties(String fileName) {
        Objects.requireNonNull(fileName, "File name cannot be null");
        try (var input = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IllegalArgumentException("File name file not found: " + fileName);
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
            throw new ProviderException("Failed to load feature flags from " + fileName, e);
        }
    }

    /**
     * Returns the prefix used for feature accessor properties.
     *
     * @return the feature accessor prefix
     */
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
