package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Represents a base command.</h1>
 * A base command is the first word typed after a slash.
 * This type of command can have subcommand types.
 */
public abstract class BaseCommandType implements CommandType {

    /**
     * Represents the list of the sub command types
     * the command can execute.
     */
    private List<CommandType> subCommandTypes = new ArrayList<>();

    /**
     * Used to add a sub command type.
     *
     * @param subCommandType The sub command type to add.
     */
    public void addSubCommandType(CommandType subCommandType) {
        this.subCommandTypes.add(subCommandType);
    }

    /**
     * Used to get the sub command types.
     *
     * @return The request list of sub command types.
     */
    public List<CommandType> getSubCommandTypes() {
        return this.subCommandTypes;
    }

    /**
     * Used to load the commands subcommands.
     */
    public void loadSubCommands() {
    }

    /**
     * Used to remove disabled sub commands.
     *
     * @param section The base command types configuration section.
     */
    public void initialiseSubCommands(ConfigurationSection section) {
        List<CommandType> toRemove = new ArrayList<>();

        for (CommandType commandType : this.subCommandTypes) {

            // Check if the configuration does not exist.
            if (!section.getKeys().contains(commandType.getName())) {
                MessageManager.log("&7[Commands] &7↳ &eDisabling &7sub command (No configuration) : " + commandType.getName());
                toRemove.add(commandType);
                continue;
            }

            // Check if the command is disabled.
            if (!section.getSection(commandType.getName()).getBoolean("enabled", true)) {
                MessageManager.log("&7[Commands] &7↳ &eDisabling &7sub command (Configuration disabled) : " + commandType.getName());
                toRemove.add(commandType);
                continue;
            }

            MessageManager.log("&7[Commands] &7↳ &aEnabling &7sub command : " + commandType.getName());
        }

        for (CommandType commandType : toRemove) {
            this.subCommandTypes.remove(commandType);
        }
    }

    /**
     * Used to remove sub commands.
     */
    public void removeSubCommands() {
        this.subCommandTypes = new ArrayList<>();
    }
}
