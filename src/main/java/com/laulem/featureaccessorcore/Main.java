package com.laulem.featureaccessorcore;

import com.laulem.featureaccessorcore.provider.InMemoryFlagProvider;
import com.laulem.featureaccessorcore.provider.LambdaFlagProvider;
import com.laulem.featureaccessorcore.provider.MultiFeatureProvider;
import com.laulem.featureaccessorcore.provider.PropertiesFileFlagProvider;
import dev.openfeature.sdk.OpenFeatureAPI;

public class Main {

    public static void main(String[] args) {
        // time
        long l = System.currentTimeMillis();
        System.out.println("Current time: " + l);
        long l2 = System.currentTimeMillis();
        System.out.println("Total time: " + (l2 - l) + "ms");
        System.out.println("----------");

        //OpenFeatureAPI.getInstance().setProvider(new PropertiesFileFlagProvider("feature-accessor.properties"));
        PropertiesFileFlagProvider propertiesFileFlagProvider = new PropertiesFileFlagProvider("feature-accessor.properties");

        InMemoryFlagProvider inMemoryFlagProvider = new InMemoryFlagProvider();
        inMemoryFlagProvider.setFlag("v2_enabled_2", true);

        LambdaFlagProvider lambdaFlagProvider = new LambdaFlagProvider();
        lambdaFlagProvider.setFlag("v2_enabled_3", () -> true);


        MultiFeatureProvider provider = new MultiFeatureProvider();
        provider.addProvider(propertiesFileFlagProvider);
        provider.addProvider(inMemoryFlagProvider);
        provider.addProvider(lambdaFlagProvider);

        OpenFeatureAPI.getInstance().setProvider(provider);
        System.out.println(OpenFeatureAPI.getInstance().getClient().getBooleanValue("v2_enabled_1", false));
        System.out.println(OpenFeatureAPI.getInstance().getClient().getBooleanValue("v2_enabled_2", false));
        System.out.println(OpenFeatureAPI.getInstance().getClient().getBooleanValue("v2_enabled_3", false));
        System.out.println(OpenFeatureAPI.getInstance().getClient().getBooleanValue("v2_enabled_4", false));
        System.out.println(OpenFeatureAPI.getInstance().getClient().getBooleanValue("new-dashboard", false));
    }
}
