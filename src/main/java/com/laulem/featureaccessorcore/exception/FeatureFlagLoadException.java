package com.laulem.featureaccessorcore.exception;

public class FeatureFlagLoadException extends RuntimeException {
    public FeatureFlagLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
