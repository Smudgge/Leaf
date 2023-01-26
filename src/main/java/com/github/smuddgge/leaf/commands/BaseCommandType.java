package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommandType implements CommandType {

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
     * Used to log the sub commands in console.
     */
    public void logSubCommands() {
        for (CommandType commandType : this.subCommandTypes) {
            MessageManager.log("&7↳ &aEnabling &7sub command : " + commandType.getName());
        }
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
    public abstract void loadSubCommands();

    /**
     * Used to remove disabled sub commands.
     *
     * @param section The base command types configuration section.
     */
    public void initialiseSubCommands(ConfigurationSection section) {
        for (CommandType commandType : this.subCommandTypes) {
            if (!section.getKeys().contains(commandType.getName())) {
                MessageManager.log("&7↳ &eDisabling &7sub command (No configuration) : " + commandType.getName());
            }
            if (!section.getSection(commandType.getName()).getBoolean("enabled", true)) {
                MessageManager.log("&7↳ &eDisabling &7sub command (Disabled) : " + commandType.getName());
            }

            MessageManager.log("&7↳ &aEnabling &7sub command : " + commandType.getName());
        }
    }

    /**
     * Used to remove sub commands.
     */
    public void removeSubCommands() {
        this.subCommandTypes = new ArrayList<>();
    }
}
