package com.laulem.featureaccessorcore.provider;

/**
 * Default implementation of PropertiesFileFlagProvider, using the feature-accessor.properties file by default, or the file specified by the feature-accessor.properties-file property.
 */
public class DefaultPropertiesFileFlagProvider extends PropertiesFileFlagProvider {
    private static final String FEATURE_ACCESSOR_PROPERTIES_FILE = "feature-accessor.properties-file";
    private static final String DEFAULT_FEATURE_ACCESSOR_PROPERTIES = "feature-accessor.properties";

    public DefaultPropertiesFileFlagProvider() {
        super(System.getProperty(FEATURE_ACCESSOR_PROPERTIES_FILE, DEFAULT_FEATURE_ACCESSOR_PROPERTIES));
    }

    /**
     * Constructs a DefaultPropertiesFileFlagProvider and loads feature flags from the specified file.
     *
     * @param fileName the name of the properties file
     */
    public DefaultPropertiesFileFlagProvider(final String fileName) {
        super(fileName);
    }
}
