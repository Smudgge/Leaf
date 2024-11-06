package com.github.smuddgge.leaf.configuration;

import com.github.squishylib.configuration.implementation.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class DatabaseConfig extends YamlConfiguration {

    public DatabaseConfig(@NotNull final File folder, @NotNull final String fileName) {
        super(folder, fileName);
    }
}
