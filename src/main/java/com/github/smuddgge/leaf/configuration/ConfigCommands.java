package com.github.smuddgge.leaf.configuration;

import com.github.smuddgge.leaf.commands.CommandAliases;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.configuration.squishyyaml.YamlConfiguration;

import java.io.File;
import java.util.Objects;

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
     * Used to get the permission used to check if a player is able to vanish.
     *
     * @return The vanishable permission.
     */
    public static String getVanishablePermission() {
        return ConfigCommands.config.getString("vanish_permission", "leaf.vanishable");
    }

    /**
     * Used to get if vanishable players can allways see vanishable players.
     *
     * @return False if not defined.
     */
    public static boolean canVanishableSeeVanishable() {
        return ConfigCommands.config.getBoolean("vanishable_can_see_vanishable", false);
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
    public static CommandAliases getCommandAliases(String identifier) {
        CommandAliases aliases = new CommandAliases();

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

    /**
     * Used to get the first command with a certain
     * command type.
     *
     * @param commandType The command type name.
     * @return An instance of a command configuration
     * section with the specified type.
     */
    public static ConfigurationSection getCommandType(String commandType) {
        for (String key : ConfigCommands.config.getSection("commands").getKeys()) {
            ConfigurationSection section = ConfigCommands.config.getSection("commands").getSection(key);
            String type = section.getString("type");
            if (Objects.equals(commandType, type)) return section;
        }
        return null;
    }
}
