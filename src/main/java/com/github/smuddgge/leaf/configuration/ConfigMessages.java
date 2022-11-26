package com.github.smuddgge.leaf.configuration;

import com.github.smuddgge.squishyyaml.YamlConfiguration;

import java.io.File;

public class ConfigMessages extends YamlConfiguration {

    private static ConfigMessages config;

    /**
     * Used to create an instance of the message's configuration file.
     *
     * @param folder The plugin's folder.
     */
    public ConfigMessages(File folder) {
        super(folder, "messages.yml");

        this.load();
    }

    /**
     * Used to initialise the message's configuration file instance.
     *
     * @param folder The plugin's folder.
     */
    public static void initialise(File folder) {
        ConfigMessages.config = new ConfigMessages(folder);
    }

    /**
     * Used to reload the message's configuration file instance.
     */
    public static void reload() {
        ConfigMessages.config.load();
    }

    /**
     * Used to get the instance of the message's configuration file.
     *
     * @return The message's configuration file.
     */
    public static ConfigMessages get() {
        return ConfigMessages.config;
    }
}
