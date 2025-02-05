package com.github.smuddgge.leaf.configuration;

import com.github.squishylib.configuration.implementation.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class Config extends YamlConfiguration {

    public Config(@NotNull final File folder, @NotNull final String fileName) {
        super(folder, fileName);
    }

    public boolean inDebugMode() {
        return this.getBoolean("logging.debug_mode");
    }

    public boolean shouldLogHeader() {
        return this.getBoolean("logging.header");
    }

    public boolean shouldLogBStats() {
        return this.getBoolean("logging.b_stats");
    }

    public @NotNull String getVanishablePermission() {
        return this.getString("vanishable_permission", "leaf.vanishable");
    }

    public boolean canVanishableSeeVanishable() {
        return this.getBoolean("can_vanishable_see_vanishable");
    }

    public @Nullable String getDiscordToken() {
        final String token = this.getString("discord_token");
        if (token == null) return null;
        return token.isEmpty() ? null : token;
    }
}
