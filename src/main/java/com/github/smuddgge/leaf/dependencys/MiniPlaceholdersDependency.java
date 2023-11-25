package com.github.smuddgge.leaf.dependencys;

/**
 * Represents the mini-placeholders dependency.
 * Contains utility methods.
 */
public class MiniPlaceholdersDependency {

    /**
     * Used to check if the mini-placeholders dependency is enabled.
     *
     * @return True if enabled.
     */
    public static boolean isEnabled() {
        try {
            Class.forName("io.github.miniplaceholders.api.MiniPlaceholders");
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Used to get the dependency message.
     *
     * @return The requested string.
     */
    public static String getDependencyMessage() {
        return "Mini Placeholders : https://modrinth.com/plugin/miniplaceholders";
    }
}
