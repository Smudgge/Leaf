package com.github.smuddgge.leaf.configuration;

import com.github.squishylib.configuration.implementation.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class WhitelistConfig extends YamlConfiguration {

    public WhitelistConfig(@NotNull final File folder, @NotNull final String fileName) {
        super(folder, fileName);
    }
}