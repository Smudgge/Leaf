package com.github.smuddgge.leaf.configuration.data;

import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.configuration.squishyyaml.YamlConfiguration;

import java.io.File;

/**
 * Represents the player data file.
 */
public class ConfigPlayers extends YamlConfiguration {

    private static ConfigPlayers config;

    /**
     * Used to create an instance of the player data file.
     *
     * @param folder The plugin's folder.
     */
    public ConfigPlayers(File folder) {
        super(folder, "data/players.yml");

        this.load();
    }

    /**
     * Used to initialise the player data configuration file.
     *
     * @param folder The plugin's folder.
     */
    public static void initialise(File folder) {
        ConfigPlayers.config = new ConfigPlayers(folder);
    }

    /**
     * Used to reload the player data configuration file.
     */
    public static void reload() {
        ConfigPlayers.config.load();
    }

    /**
     * Used to get the instance of the player data configuration file.
     *
     * @return The player data configuration file.
     */
    public static ConfigPlayers get() {
        return ConfigPlayers.config;
    }
}