package com.github.smuddgge.leaf.configuration;

import com.github.smuddgge.leaf.configuration.squishyyaml.YamlConfiguration;

import java.io.File;

/**
 * Represents the database configuration file.
 */
public class ConfigDatabase extends YamlConfiguration {

    private static ConfigDatabase config;

    /**
     * Used to create an instance of the database's configuration file.
     *
     * @param folder The plugin's folder.
     */
    public ConfigDatabase(File folder) {
        super(folder, "database.yml");

        this.load();
    }

    /**
     * Used to initialise the database's configuration file instance.
     *
     * @param folder The plugin's folder.
     */
    public static void initialise(File folder) {
        ConfigDatabase.config = new ConfigDatabase(folder);
    }

    /**
     * Used to reload the database's configuration file instance.
     */
    public static void reload() {
        ConfigDatabase.config.load();
    }

    /**
     * Used to get the instance of the database's configuration file.
     *
     * @return The database's configuration file.
     */
    public static ConfigDatabase get() {
        return ConfigDatabase.config;
    }

    /**
     * Used to get weather the database should be in debug mode.
     *
     * @return False by default.
     */
    public static boolean isDebugMode() {
        return ConfigDatabase.get().getBoolean("debugmode", false);
    }
}
