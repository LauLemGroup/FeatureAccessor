package com.laulem.featureaccessorcore.exception;

/**
 * Exception thrown when a provider encounters an error.
 */
public class ProviderException extends RuntimeException {
    public ProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
