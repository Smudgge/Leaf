package com.github.smuddgge.leaf.configuration;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a list of configuration keys.
 */
public enum ConfigurationKey {
    DISCORD_WEBHOOK("discord_webhook");

    private final @NotNull String key;

    /**
     * Used to create a configuration key.
     *
     * @param key The value of the key.
     */
    ConfigurationKey(@NotNull String key) {
        this.key = key;
    }

    /**
     * Used to get the value of the key.
     *
     * @return The value of the key.
     */
    public @NotNull String getKey() {
        return this.key;
    }
}
