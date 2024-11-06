package com.github.smuddgge.leaf.configuration;

import com.github.squishylib.configuration.implementation.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Config extends YamlConfiguration {

    public Config(@NotNull final File folder, @NotNull final String fileName) {
        super(folder, fileName);
    }

    public boolean inDebugMode() {
        return this.getBoolean("logging.debug_mode");
    }

    public boolean shouldLogBStats() {
        return this.getBoolean("logging.b_stats");
    }
}
