package com.github.smuddgge.leaf.configuration;

import com.github.smuddgge.squishyyaml.YamlConfiguration;

import java.io.File;

/**
 * Represents the command configuration file.
 */
public class ConfigCommands extends YamlConfiguration {

    private static ConfigCommands config;

    /**
     * Used to create an instance of the command's configuration file.
     *
     * @param folder The plugin's folder.
     */
    public ConfigCommands(File folder) {
        super(folder, "commands.yml");

        this.load();
    }

    /**
     * Used to initialise the command's configuration file instance.
     *
     * @param folder The plugin's folder.
     */
    public static void initialise(File folder) {
        ConfigCommands.config = new ConfigCommands(folder);
    }

    /**
     * Used to reload the command's configuration file instance.
     */
    public static void reload() {
        ConfigCommands.config.load();
    }

    /**
     * Used to get the instance of the command's configuration file.
     *
     * @return The command's configuration file.
     */
    public static ConfigCommands get() {
        return ConfigCommands.config;
    }
}
