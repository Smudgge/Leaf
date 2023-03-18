package com.github.smuddgge.leaf.configuration;

import com.github.smuddgge.leaf.configuration.handlers.CommandsConfigurationHandler;
import com.github.smuddgge.leaf.configuration.handlers.PlaceholderConfigurationHandler;

import java.io.File;

/**
 * Manages all the configuration files.
 */
public class ConfigurationManager {

    private static File folder;

    private static CommandsConfigurationHandler commandsConfigurationHandler;
    private static PlaceholderConfigurationHandler placeholderConfigurationHandler;

    /**
     * Used to initialise the configuration manager.
     *
     * @param folder The plugin's folder.
     */
    public static void initialise(File folder) {
        ConfigurationManager.folder = folder;

        ConfigurationManager.commandsConfigurationHandler = new CommandsConfigurationHandler(folder);
        ConfigurationManager.placeholderConfigurationHandler = new PlaceholderConfigurationHandler(folder);

        ConfigMain.initialise(folder);
        ConfigDatabase.initialise(folder);
        ConfigMessages.initialise(folder);
    }

    /**
     * Used to reload all the configuration files.
     */
    public static void reload() {
        ConfigurationManager.getCommands().reload();
        ConfigurationManager.getPlaceholders().reload();

        ConfigMain.initialise(folder);
        ConfigDatabase.initialise(folder);
        ConfigMessages.initialise(folder);
    }

    public static CommandsConfigurationHandler getCommands() {
        return ConfigurationManager.commandsConfigurationHandler;
    }

    public static PlaceholderConfigurationHandler getPlaceholders() {
        return ConfigurationManager.placeholderConfigurationHandler;
    }
}
