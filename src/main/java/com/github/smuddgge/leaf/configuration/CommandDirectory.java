package com.github.smuddgge.leaf.configuration;

import com.github.squishylib.configuration.directory.ConfigurationDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CommandDirectory extends ConfigurationDirectory {

    public CommandDirectory(@NotNull File directory) {
        super(directory);
    }
}
