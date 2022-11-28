package com.github.smuddgge.leaf.configuration;

import com.github.smuddgge.leaf.commands.Aliases;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.configuration.squishyyaml.YamlConfiguration;

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

    /**
     * Used to get a command's section from the configuration file.
     *
     * @param identifier The identifier of the command.
     * @return The instance of the configuration section.
     */
    public static ConfigurationSection getCommand(String identifier) {
        return ConfigCommands.get().getSection("commands").getSection(identifier);
    }

    /**
     * Used to get the command's name from the configuration file.
     *
     * @param identifier The identifier of the command.
     * @return The commands name.
     */
    public static String getCommandName(String identifier) {
        return ConfigCommands.getCommand(identifier).getString("name");
    }

    /**
     * Used to get the command's aliases from the configuration file.
     *
     * @param identifier The identifier of the command.
     * @return The commands list of aliases.
     */
    public static Aliases getCommandAliases(String identifier) {
        Aliases aliases = new Aliases();

        aliases.append(ConfigCommands.getCommand(identifier).getListString("aliases"));

        return aliases;
    }

    /**
     * Used to get the command's required permission from the configuration file.
     *
     * @param identifier The identifier of the command.
     * @return The commands required permission.
     */
    public static String getCommandPermission(String identifier) {
        return ConfigCommands.getCommand(identifier).getString("permission");
    }

    /**
     * Used to get if the command is enabled.
     *
     * @param identifier The identifier of the command.
     * @return True if the command is enabled.
     */
    public static boolean isCommandEnabled(String identifier) {
        return ConfigCommands.getCommand(identifier).getBoolean("enabled", true);
    }
}
