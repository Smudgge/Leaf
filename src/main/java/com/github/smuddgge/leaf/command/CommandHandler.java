package com.github.smuddgge.leaf.command;

import com.github.smuddgge.leaf.Leaf;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Contains all the registered commands and
 * handles execution of commands.
 */
public class CommandHandler {

    private final List<Command> commands = new ArrayList<>();
    private final List<BaseCommandType> commandTypes = new ArrayList<>();
    private final List<String> registeredCommandNames = new ArrayList<>();

    public @Nullable BaseCommandType getCommandType(@NotNull String identifier) {
        for (final BaseCommandType commandType : this.commandTypes) {
            if (commandType.getIdentifier().equals(identifier)) return commandType;
        }

        return null;
    }

    public @Nullable Command getCommand(String name) {
        for (Command command : this.commands) {
            if (command.getName().equals(name)) return command;
            if (command.getAliases()) return command;
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
                MessageManager.log("&7[Commands] " + command.getIdentifier() + " is disabled in the configuration file.");
                continue;
            }

            // Log enable message
            MessageManager.log("&7[Commands] &aEnabling &7command : " + command.getName());

            // Load subcommands
            command.getBaseCommandType().loadSubCommands();
            command.getBaseCommandType().initialiseSubCommands(command.getSection());

            // Check if the command is valid
            if (command.getName() == null) {
                MessageManager.warn("&7[Commands] &f[command] &e: Command name not specified in the configuration file."
                        .replace("[command]", command.getIdentifier()));
                continue;
            }

            // Register main command name
            manager.register(manager.metaBuilder(command.getName()).build(), command);
            this.registeredCommandNames.add(command.getName());

            // Register aliases if they exist
            if (command.getAliases().get().isEmpty()) continue;

            for (String alias : command.getAliases().get()) {
                manager.register(manager.metaBuilder(alias).build(), command);
                this.registeredCommandNames.add(alias);
            }
        }
    }

    /**
     * Used to unregister all the commands by
     * this plugin in the proxy server.
     */
    public void unregister() {
        CommandManager manager = Leaf.getServer().getCommandManager();

        for (String commandName : this.registeredCommandNames) {
            // Unregister the command
            manager.unregister(commandName);
        }

        for (Command command : this.commands) {
            // Remove subcommands
            command.getBaseCommandType().removeSubCommands();
        }

        this.registeredCommandNames = new ArrayList<>();
        this.commands = new ArrayList<>();
    }

    /**
     * Used to check if a command string is runnable in this handler.
     *
     * @param commandString The instance of the command string.
     * @return True if it is runnable.
     */
    public boolean isRunnable(@NotNull String commandString) {
        String base = commandString.split(" ")[0];

        for (Command command : this.commands) {
            if (command.getName().equals(base)) return true;
            if (command.getAliases().contains(base)) return true;
        }

        return false;
    }

    /**
     * Used to execute a command.
     *
     * @param commandString The command to execute.
     */
    public void execute(Player player, String commandString) {
        if (Objects.equals(commandString, "")) return;

        // Get the identifier.
        String name = commandString.split(" ")[0];
        String[] arguments = commandString.substring(name.length()).trim().split(" ");

        // Get the instance of the command.
        Command command = this.getCommand(name);

        // Execute the command as a player.
        command.onPlayerRun(arguments, new User(player));
    }

    public boolean isEmpty() {
        return this.commands.isEmpty();
    }
}
