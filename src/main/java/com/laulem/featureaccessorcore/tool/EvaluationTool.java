package com.laulem.featureaccessorcore.tool;

import dev.openfeature.sdk.ErrorCode;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Value;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class EvaluationTool {

    public static final String STATIC_REASON = "STATIC";
    public static final String DEFAULT_REASON = "DEFAULT";
    public static final String ERROR_REASON = "ERROR";

    private EvaluationTool() {
    }

    /**
     * Evaluates a flag from a map of flags using the provided extractor function.
     *
     * @param flags the map of flag keys to Value
     * @param flagKey the key of the flag to evaluate
     * @param defaultValue the default value to return if the flag is not found or an error occurs
     * @param ctx the evaluation context (not used)
     * @param extractor function to extract the real value from the Value object
     * @return the ProviderEvaluation result
     * @param <T> the type of the flag value expected
     */
    public static <T> ProviderEvaluation<T> evaluateFlag(Map<String, Value> flags, String flagKey, T defaultValue, EvaluationContext ctx, Function<Value, T> extractor) {
        try {
            Value value = flags.get(flagKey.toUpperCase());
            if (value != null) {
                return ProviderEvaluation.<T>builder()
                        .reason(STATIC_REASON)
                        .value(extractor.apply(value))
                        .build();
            }

            return ProviderEvaluation.<T>builder()
                    .reason(DEFAULT_REASON)
                    .value(defaultValue)
                    .errorCode(ErrorCode.FLAG_NOT_FOUND)
                    .build();
        } catch (Exception e) {
            return ProviderEvaluation.<T>builder()
                    .reason(ERROR_REASON)
                    .value(defaultValue)
                    .errorCode(ErrorCode.PARSE_ERROR)
                    .build();
        }
    }

    /**
     * Evaluates a flag from a map of flag suppliers using the provided extractor function.
     *
     * @param flags the map of flag keys to Supplier<Value>
     * @param flagKey the key of the flag to evaluate
     * @param defaultValue the default value to return if the flag is not found or an error occurs
     * @param ctx the evaluation context (not used)
     * @param extractor function to extract the value from the Supplier<Value>
     * @return the ProviderEvaluation result
     * @param <T> the type of the flag value expected
     */
    public static <T> ProviderEvaluation<T> evaluateFlagSupplier(Map<String, Supplier<Value>> flags, String flagKey, T defaultValue, EvaluationContext ctx, Function<Supplier<Value>, T> extractor) {
        try {
            Supplier<Value> value = flags.get(flagKey.toUpperCase());
            if (value != null) {
                return ProviderEvaluation.<T>builder()
                        .reason(STATIC_REASON)
                        .value(extractor.apply(value))
                        .build();
            }

            return ProviderEvaluation.<T>builder()
                    .reason(DEFAULT_REASON)
                    .value(defaultValue)
                    .errorCode(ErrorCode.FLAG_NOT_FOUND)
                    .build();
        } catch (Exception e) {
            return ProviderEvaluation.<T>builder()
                    .reason(ERROR_REASON)
                    .value(defaultValue)
                    .errorCode(ErrorCode.PARSE_ERROR)
                    .build();
        }
    }
}
