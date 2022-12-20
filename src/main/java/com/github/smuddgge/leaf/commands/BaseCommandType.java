package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.MessageManager;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommandType implements CommandType {

    private final List<CommandType> subCommandTypes = new ArrayList<>();

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
}
