package com.github.smuddgge.leaf.configuration;

import com.github.squishylib.configuration.directory.ConfigurationDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PlaceholderDirectory extends ConfigurationDirectory {

    public PlaceholderDirectory(@NotNull File directory) {
        super(directory);
    }
}
