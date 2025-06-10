package com.laulem.featureaccessorcore.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvResolver {
    private static final Pattern ENV_PATTERN = Pattern.compile("\\$\\{([A-Z0-9_]+)(?::([^}]*))?}");

    private EnvResolver() {
    }

    public static String resolveEnvVars(String input) {
        if (input == null || !input.contains("${")) return input;

        Matcher matcher = ENV_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String varName = matcher.group(1);           // ex: API_URL
            String defaultValue = matcher.group(2);      // ex: http://localhost
            String resolved = System.getenv(varName);    // env value

            if (resolved == null) {
                resolved = (defaultValue != null) ? defaultValue : "";
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(resolved));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
}
