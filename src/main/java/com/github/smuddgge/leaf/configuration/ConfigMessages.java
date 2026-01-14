package com.github.smuddgge.leaf.configuration;


import com.github.smuddgge.squishyconfiguration.implementation.yaml.YamlConfiguration;

import java.io.File;

/**
 * Represents the message's configuration file.
 */
public class ConfigMessages extends YamlConfiguration {

    private static ConfigMessages config;

    /**
     * Used to create an instance of the message's configuration file.
     *
     * @param folder The plugin's folder.
     */
    public ConfigMessages(File folder) {
        super(folder, "messages.yml");
        this.setDefaultPath("messages.yml");
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
     * Used to get the instance of the message's configuration file.
     *
     * @return The message's configuration file.
     */
    public static ConfigMessages get() {
        return ConfigMessages.config;
    }

    /**
     * Used to get the incorrect argument message.
     *
     * @param commandSyntax The command's syntax.
     * @return The incorrect argument message.
     */
    public static String getIncorrectArguments(String commandSyntax) {
        return ConfigMessages.config.getSection("messages")
                .getString("incorrect_arguments", "{error} Incorrect arguments. %command%")
                .replace("%command%", commandSyntax);
    }

    /**
     * Used to get the database error message.
     *
     * @return The database error message.
     */
    public static String getDatabaseDisabled() {
        return ConfigMessages.config.getSection("messages")
                .getString("database_disabled", "{error_colour}Database Disabled.");
    }

    /**
     * Used to get the database empty message.
     *
     * @return The database empty message.
     */
    public static String getDatabaseEmpty() {
        return ConfigMessages.config.getSection("messages")
                .getString("database_empty", "{error_colour}There are no records in the database.");
    }

    /**
     * Used to get the error message that is sent
     * when console runs a player online command.
     *
     * @return The requested player command message.
     */
    public static String getPlayerCommand() {
        return ConfigMessages.config.getSection("messages")
                .getString("player_command", "{error_colour}This command can only be run by the player.");
    }

    /**
     * Used to get the message sent when an error occurs.
     *
     * @return The requested error message.
     */
    public static String getError() {
        return ConfigMessages.config.getSection("messages")
                .getString("error", "{error_colour}Error occurred while running command.");
    }

    /**
     * Used to get the no permission message.
     *
     * @return The requested no permission message.
     */
    public static String getNoPermission() {
        return ConfigMessages.config.getSection("messages")
                .getString("no_permission", "{error_colour}No permission.");
    }

    /**
     * Used to get the is limited message.
     *
     * @return The requested is limited message.
     */
    public static String getIsLimited() {
        return ConfigMessages.config.getSection("messages")
                .getString("is_limited", "{error_colour}You cannot execute this command anymore as you have reached the limit.");
    }

    public static String getOnCooldown() {
        return ConfigMessages.config.getSection("messages")
                .getString("on_cooldown", "{error_colour}Please wait %cooldown% before executing this command again.");
    }
}
