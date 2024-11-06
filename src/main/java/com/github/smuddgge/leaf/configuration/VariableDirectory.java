package com.github.smuddgge.leaf.configuration;

import com.github.squishylib.configuration.directory.ConfigurationDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class VariableDirectory extends ConfigurationDirectory {

    public VariableDirectory(@NotNull File directory) {
        super(directory);
    }
}
