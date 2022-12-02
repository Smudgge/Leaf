package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a command.
 */
public class Command implements SimpleCommand {

    private final String identifier;

    private final CommandType commandType;

    /**
     * Used to create a command.
     *
     * @param identifier The command's identifier in the configuration.
     */
    public Command(String identifier, CommandType commandType) {
        this.identifier = identifier;
        this.commandType = commandType;
    }

    /**
     * Used to get the command's syntax.
     *
     * @return The command's syntax.
     */
    public String getSyntax() {
        return this.commandType.getSyntax();
    }

    /**
     * Used to get the tab suggestions.
     *
     * @param user The user completing the command.
     * @return The command's argument suggestions.
     */
    public CommandSuggestions getSuggestions(User user) {
        return this.commandType.getSuggestions(user);
    }

    /**
     * Executed when the command is run in the console.
     *
     * @param arguments The arguments given in the command.
     * @return The command's status.
     */
    public CommandStatus onConsoleRun(String[] arguments) {
        return this.commandType.onConsoleRun(this.getSection(), arguments);
    }

    /**
     * Executed when a player runs the command.
     *
     * @param arguments The arguments given in the command.
     * @param user      The instance of the user running the command.
     * @return The command's status.
     */
    public CommandStatus onPlayerRun(String[] arguments, User user) {
        return this.commandType.onPlayerRun(this.getSection(), arguments, user);
    }

    /**
     * Used to get the command's identifier.
     *
     * @return Commands identifier.
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Used to get the command's configuration section.
     *
     * @return The configuration section.
     */
    public ConfigurationSection getSection() {
        return ConfigCommands.getCommand(this.identifier);
    }

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
    public CommandAliases getAliases() {
        return ConfigCommands.getCommandAliases(this.getIdentifier());
    }

    /**
     * Used to get the permission to execute the command.
     *
     * @return Command permission.
     */
    public String getPermission() {
        return ConfigCommands.getCommandPermission(this.getIdentifier());
    }

    /**
     * Used to get if the command is enabled.
     *
     * @return True if the command is enabled.
     */
    public boolean isEnabled() {
        return ConfigCommands.isCommandEnabled(this.getIdentifier());
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();

        if (source instanceof Player) {
            User user = new User((Player) source);
            CommandStatus status = this.onPlayerRun(invocation.arguments(), user);

            if (status.hasIncorrectArguments()) {
                user.sendMessage(ConfigMessages.getIncorrectArguments(this.getSyntax())
                        .replace("[name]", this.getName()));
            }

            return;
        }

        CommandStatus status = this.onConsoleRun(invocation.arguments());

        if (status.hasIncorrectArguments()) {
            MessageManager.log(ConfigMessages.getIncorrectArguments(this.getSyntax())
                    .replace("[name]", this.getName()));
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        String permission = this.getPermission();

        if (permission == null) return true;

        return invocation.source().hasPermission(this.getPermission());
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        CommandSource source = invocation.source();

        if (source instanceof Player) {

            int index = invocation.arguments().length - 1;
            if (index == -1) index = 0;

            CommandSuggestions suggestions = this.getSuggestions(new User((Player) source));

            if (suggestions == null) return CompletableFuture.completedFuture(List.of());
            if (suggestions.get() == null) return CompletableFuture.completedFuture(List.of());
            if (suggestions.get().size() <= index) return CompletableFuture.completedFuture(List.of());

            List<String> list = suggestions.get().get(index);

            if (list == null) return CompletableFuture.completedFuture(List.of());

            return CompletableFuture.completedFuture(list);

        }

        return CompletableFuture.completedFuture(List.of());
    }
}
