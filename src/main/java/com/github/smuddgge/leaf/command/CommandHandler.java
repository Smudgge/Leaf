package com.github.smuddgge.leaf.command;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.logger.Logger;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all the active commands.
 * <p>
 * Add commands or command types to this class and
 * register them with {@link CommandHandler#registerCommands()}.
 * <p>
 * The end point for command registering.
 */
public class CommandHandler {

    private final @NotNull List<Command> commands;
    private final @NotNull List<BaseCommandType> commandTypes;
    private final @NotNull List<String> registeredCommandNames;

    public CommandHandler() {
        this.commands = new ArrayList<>();
        this.commandTypes = new ArrayList<>();
        this.registeredCommandNames = new ArrayList<>();
    }

    public @Nullable BaseCommandType getCommandType(@NotNull String identifier) {
        for (final BaseCommandType commandType : this.commandTypes) {
            if (commandType.getIdentifier().equals(identifier)) return commandType;
        }
        return null;
    }

    public @Nullable Command getCommand(@NotNull String name) {
        for (Command command : this.commands) {
            if (command.getName().equals(name)) return command;
            if (command.getAliases().contains(name)) return command;
        }
        return null;
    }

    public void addCommandType(@NotNull BaseCommandType commandType) {
        Leaf.get().getLogger().debug(" [Commands] Added command type &f" + commandType.getIdentifier());
        this.commandTypes.add(commandType);
    }

    public void addCommand(@NotNull Command command) {
        this.commands.add(command);
    }

    /**
     * Registers the list of commands with the velocity proxy.
     * <p>
     * This does not do anything with command types.
     */
    public void registerCommands() {

        // Set up the commands logger.
        final Logger logger = Leaf.get().getLogger().extend(" &7[Commands]");

        // Get the velocity command manager.
        final CommandManager manager = Leaf.get().getProxyServer().getCommandManager();

        // Loop though commands.
        for (Command command : this.commands) {

            // Check if command is enabled
            if (!command.isEnabled()) {
                logger.optional(Logger.Opt.COMMANDS, command.getIdentifier() + " is disabled in the configuration file.");
                continue;
            }

            // Check if the command is valid.
            if (command.getName().equals("null-command")) {
                logger.warn("You have a command with no name o: &7[identifier: &f" + command.getIdentifier() + "&7]");
                continue;
            }

            // Log enable message.
            logger.optional(Logger.Opt.COMMANDS, "&aEnabling &7" + command.getIdentifier());

            // Load subcommands.
            command.getCommandType().loadSubCommands();
            command.getCommandType().initialiseSubCommands(command.getSection());

            // Register main command name.
            manager.register(manager.metaBuilder(command.getName()).build(), command);
            this.registeredCommandNames.add(command.getName());

            // Register aliases if they exist.
            for (String alias : command.getAliases()) {
                manager.register(manager.metaBuilder(alias).build(), command);
                this.registeredCommandNames.add(alias);
            }
        }
    }

    public void unregisterCommands() {

        // Get the velocity command manager.
        final CommandManager manager = Leaf.get().getProxyServer().getCommandManager();

        // Unregister all registered commands.
        for (String commandName : this.registeredCommandNames) {
            manager.unregister(commandName);
        }

        // Remove all sub commands that have been initialised.
        // This is because the configuration could have changed.
        for (Command command : this.commands) {
            command.getCommandType().removeAllSubCommands();
        }

        this.registeredCommandNames.clear();
        this.commands.clear();
    }

    /**
     * Used to check if a command string can be run in this handler.
     * <p>
     * It checks if the commands name is registered.
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
     * Used to execute a command with this handler.
     *
     * @param commandString The command to execute.
     */
    public void execute(@NotNull Player player, @NotNull String commandString) {

        // Is the command empty?
        if (commandString.isEmpty()) return;

        // Get the identifier.
        String name = commandString.split(" ")[0];
        String[] arguments = commandString.substring(name.length()).trim().split(" ");

        // Get the instance of the command.
        Command command = this.getCommand(name);
        if (command == null) return;

        // Execute the command as a player.
        command.execute(arguments, player);
    }

    public boolean isEmpty() {
        return this.commands.isEmpty();
    }
}
