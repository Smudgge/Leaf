package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;

public interface CommandType {

    /**
     * Used to get the command type name.
     *
     * @return The command type name.
     */
    String getName();

    /**
     * Used to get the command's syntax.
     *
     * @return The command's syntax.
     */
    String getSyntax();

    /**
     * Used to get the tab suggestions.
     *
     * @param section The configuration section.
     * @param user    The user completing the command.
     * @return The command's argument suggestions.
     */
    CommandSuggestions getSuggestions(ConfigurationSection section, User user);

    /**
     * Executed when the command is run in the console.
     *
     * @param section   The command's configuration section.
     * @param arguments The arguments given in the command.
     * @return The command's status.
     */
    CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments);

    /**
     * Executed when a player runs the command.
     *
     * @param section   The command's configuration section.
     * @param arguments The arguments given in the command.
     * @param user      The instance of the user running the command.
     * @return The command's status.
     */
    CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user);
}
