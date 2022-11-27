package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a command
 */
public abstract class Command implements SimpleCommand {

    /**
     * Used to get the command's identifier.
     *
     * @return Commands identifier.
     */
    public abstract String getIdentifier();

    /**
     * Used to get the name of the command.
     *
     * @return The name of the command.
     */
    public String getName() {
        return ConfigCommands.getCommandName(this.getIdentifier());
    }

    /**
     * Used to get the commands aliases.
     * These are other command names that will execute this command.
     *
     * @return The list of aliases.
     */
    public Aliases getAliases() {
        return ConfigCommands.getCommandAliases(this.getIdentifier());
    }

    /**
     * Used to get the tab suggestions.
     *
     * @param user The user completing the command.
     * @return The command's argument suggestions.
     */
    public abstract Suggestions getSuggestions(User user);

    /**
     * Used to get the permission to execute the command.
     *
     * @return Command permission.
     */
    public String getPermission() {
        return ConfigCommands.getCommandPermission(this.getIdentifier());
    }

    /**
     * Executed when the command is run in the console.
     *
     * @param arguments The arguments given in the command.
     * @return The command's status.
     */
    public abstract CommandStatus onConsoleRun(String[] arguments);

    /**
     * Executed when a player runs the command.
     *
     * @param arguments The arguments given in the command.
     * @param user      The instance of the user running the command.
     * @return The command's status.
     */
    public abstract CommandStatus onPlayerRun(String[] arguments, User user);

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();

        if (source instanceof Player) {
            this.onPlayerRun(invocation.arguments(), new User((Player) source));
            return;
        }

        this.onConsoleRun(invocation.arguments());
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission(this.getPermission());
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        CommandSource source = invocation.source();

        if (source instanceof Player) {
            this.onPlayerRun(invocation.arguments(), new User((Player) source));

            List<String> list = new ArrayList<>();

            list.add("Test");

            return CompletableFuture.completedFuture(list);

        }

        return null;
    }
}
