package com.github.smuddgge.leaf.configuration;

import com.github.squishylib.configuration.implementation.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MessagesConfig extends YamlConfiguration {

    public MessagesConfig(@NotNull final File folder, @NotNull final String fileName) {
        super(folder, fileName, MessagesConfig.class);
    }

    public @NotNull String incorrectArguments() {
        return this.getString("incorrect_arguments", "{error_colour}Incorrect arguments. %command%");
    }

    public @NotNull String databaseDisabled() {
        return this.getString("database_disabled", "{error_colour}Database disabled.");
    }

    public @NotNull String databaseEmpty() {
        return this.getString("database_empty", "{error_colour}There are no records in the database.");
    }

    public @NotNull String playerCommand() {
        return this.getString("player_command", "{error_colour}This command can only be run by the player.");
    }

    public @NotNull String error() {
        return this.getString("error", "{error_colour}Error occurred while running command.");
    }

    public @NotNull String noPermission() {
        return this.getString("no_permission", "{error_colour}You do not have permission to run this command.");
    }

    public @NotNull String isLimited() {
        return this.getString("is_limited", "{error_colour}You cannot execute this command anymore as you have reached the limit.");
    }

    public @NotNull String onCooldown() {
        return this.getString("on_cooldown", "{error_colour}Please wait %cooldown% before executing this command again.");
    }
}
