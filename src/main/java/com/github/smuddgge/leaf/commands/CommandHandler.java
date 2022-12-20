package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.velocitypowered.api.command.CommandManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the command handler.
 */
public class CommandHandler {

    private List<Command> commands = new ArrayList<>();

    private final List<BaseCommandType> commandTypes = new ArrayList<>();

    private List<String> registeredCommands = new ArrayList<>();

    /**
     * Used to append a command type to the command handler.
     *
     * @param command The command to append.
     */
    public void append(Command command) {
        this.commands.add(command);
    }

    /**
     * Used to add a type of command to the list.
     *
     * @param commandType Instance of the command type.
     */
    public void addType(BaseCommandType commandType) {
        this.commandTypes.add(commandType);
    }

    /**
     * Used to get a command type.
     *
     * @param name The name of the command type to get.
     * @return The command type instance.
     */
    public BaseCommandType getType(String name) {
        for (BaseCommandType commandType : this.commandTypes) {
            if (Objects.equals(commandType.getName(), name)) return commandType;
        }

        return null;
    }

    /**
     * Used to register the commands with the proxy server.
     */
    public void register() {
        CommandManager manager = Leaf.getServer().getCommandManager();

        for (Command command : this.commands) {
            // Check if command is enabled
            if (!command.isEnabled()) {
                MessageManager.log(command.getIdentifier() + " is not enabled in configuration, therefore disabling.");
                continue;
            }

            // Log enable message
            MessageManager.log("&aEnabling &7command : " + command.getName());
            command.getBaseCommandType().logSubCommands();

            // Check if the command is valid
            if (command.getName() == null) {
                MessageManager.warn("&eUnregistering command &f[command]. &eCommand name not specified in the configuration file."
                        .replace("[command]", command.getIdentifier()));
                continue;
            }

            // Register main command name
            manager.register(manager.metaBuilder(command.getName()).build(), command);
            this.registeredCommands.add(command.getName());

            // Register aliases if they exist
            if (command.getAliases() == null) continue;

            for (String alias : command.getAliases().get()) {
                manager.register(manager.metaBuilder(alias).build(), command);
                this.registeredCommands.add(alias);
            }
        }
    }

    /**
     * Used to unregister all the commands by
     * this plugin in the proxy server.
     */
    public void unregister() {
        CommandManager manager = Leaf.getServer().getCommandManager();

        for (String commandName : this.registeredCommands) {
            // Unregister the command
            manager.unregister(commandName);
        }

        this.registeredCommands = new ArrayList<>();
        this.commands = new ArrayList<>();
    }
}
