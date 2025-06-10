package com.laulem.featureaccessorcore.provider;

public class DefaultPropertiesFileFlagProvider extends PropertiesFileFlagProvider {
    private static final String FEATURE_ACCESSOR_PROPERTIES_FILE = "feature-accessor.properties-file";
    private static final String DEFAULT_FEATURE_ACCESSOR_PROPERTIES = "feature-accessor.properties";

    public DefaultPropertiesFileFlagProvider() {
        super(System.getProperty(FEATURE_ACCESSOR_PROPERTIES_FILE, DEFAULT_FEATURE_ACCESSOR_PROPERTIES));
    }

    public DefaultPropertiesFileFlagProvider(final String fileName) {
        super(fileName);
    }
}
