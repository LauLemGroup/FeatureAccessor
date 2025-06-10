# FeatureAccessorCore

FeatureAccessorCore is a Java library designed to provide a flexible and extensible way to manage feature flags and configuration properties in your applications. It supports multiple providers, including property files and in-memory flags, and is built to integrate seamlessly with modern Java projects.

## Features

- **Feature Flag Management**: Provides a way to enable or disable features at runtime.
- **Providers**: Supports different sources for feature flags, such as:
  - Properties File Provider (`PropertiesFileFlagProvider`): Reads flags from a properties file.
  - In-Memory Provider (`InMemoryFlagProvider`): Stores flags in memory for testing or dynamic use.
  - Lambda/Custom Provider (`LambdaFlagProvider`): Allows custom logic for flag evaluation with lambda.
  - Support for combining multiple providers (`MultiFeatureProvider`): Enables chaining or prioritizing several providers to resolve feature flags from different sources.
- **Integration with OpenFeature**: Compatible with the OpenFeature SDK for interoperability.

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Installation
Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.laulem</groupId>
    <artifactId>feature-accessor-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Usage

1. **Configure your feature flags** in a properties file (e.g., `feature-accessor.properties`).
2. **Initialize the provider** in your Java code:

```properties
# feature-accessor.properties
feature-accessor.feature_XXX=true
```

```java
// Example usage
OpenFeatureAPI.getInstance().setProvider(new PropertiesFileFlagProvider("feature-accessor.properties"));
boolean isEnabled = OpenFeatureAPI.getInstance().getClient().getBooleanValue("feature_XXX", false);
```

3. **Switch providers** or extend with your own by implementing the provider interface.

## Project Structure

- `src/main/java/com/laulem/featureaccessorcore` - Core library source code
- `src/test/java/com/laulem/featureaccessorcore` - Unit tests

## Contributing

Contributions are welcome! Please open issues or submit pull requests via [GitHub](https://github.com/LauLemGroup/FeatureAccessor).

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](https://www.apache.org/licenses/LICENSE-2.0) file for details.

## Contact

For questions or support, please open an issue on GitHub or contact the maintainers.

## Author

**LEMAIRE Alexandre**
- Website: [https://www.laulem.com](https://www.laulem.com)
- Mail: [alexandre.lemaire@laulem.com](mailto:alexandre.lemaire@laulem.com)
