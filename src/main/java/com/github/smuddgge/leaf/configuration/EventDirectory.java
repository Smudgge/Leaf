package com.github.smuddgge.leaf.configuration;

import com.github.squishylib.configuration.directory.ConfigurationDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class EventDirectory extends ConfigurationDirectory {

    public EventDirectory(@NotNull File directory) {
        super(directory);
    }
}
