package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a command.
 */
public class Command implements SimpleCommand {

    private final String identifier;

    private final BaseCommandType commandType;

    /**
     * Used to create a command.
     *
     * @param identifier The command's identifier in the configuration.
     */
    public Command(String identifier, BaseCommandType commandType) {
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
     * @param section The configuration section.
     * @param user    The user completing the command.
     * @return The command's argument suggestions.
     */
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return this.commandType.getSuggestions(section, user);
    }

    /**
     * Executed when the command is run in the console.
     *
     * @param arguments The arguments given in the command.
     * @return The command's status.
     */
    public CommandStatus onConsoleRun(String[] arguments) {
        if (this.commandType.getSubCommandTypes().isEmpty() || arguments.length <= 0)
            return this.commandType.onConsoleRun(this.getSection(), arguments);

        for (CommandType commandType : this.commandType.getSubCommandTypes()) {
            String name = arguments[0];

            List<String> subCommandNames = new ArrayList<>();
            subCommandNames.add(this.getSection().getSection(commandType.getName()).getString("name", commandType.getName()));
            subCommandNames.addAll(this.getSection().getSection(commandType.getName()).getListString("aliases", new ArrayList<>()));

            if (subCommandNames.contains(name))
                return commandType.onConsoleRun(this.getSection(), arguments);
        }

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
        if (this.commandType.getSubCommandTypes().isEmpty() || arguments.length <= 0)
            return this.commandType.onPlayerRun(this.getSection(), arguments, user);

        for (CommandType commandType : this.commandType.getSubCommandTypes()) {
            String name = arguments[0];

            List<String> subCommandNames = new ArrayList<>();
            subCommandNames.add(this.getSection().getSection(commandType.getName()).getString("name", commandType.getName()));
            subCommandNames.addAll(this.getSection().getSection(commandType.getName()).getListString("aliases", new ArrayList<>()));

            if (subCommandNames.contains(name))
                return commandType.onPlayerRun(this.getSection(), arguments, user);
        }

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
     * Used to get the base command type.
     *
     * @return The base command type.
     */
    public BaseCommandType getBaseCommandType() {
        return this.commandType;
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

            // Run the command as a player.
            CommandStatus status = this.onPlayerRun(invocation.arguments(), user);

            if (status.hasIncorrectArguments()) {
                user.sendMessage(ConfigMessages.getIncorrectArguments(this.getSyntax())
                        .replace("[name]", this.getName()));
            }
            if (status.hasDatabaseDisabled()) user.sendMessage(ConfigMessages.getDatabaseDisabled());
            if (status.hasDatabaseEmpty()) user.sendMessage(ConfigMessages.getDatabaseEmpty());
            if (status.hasPlayerCommand()) user.sendMessage(ConfigMessages.getPlayerCommand());

            return;
        }


        // Run the command in console.
        CommandStatus status = this.onConsoleRun(invocation.arguments());

        if (status.hasIncorrectArguments()) {
            MessageManager.log(ConfigMessages.getIncorrectArguments(this.getSyntax())
                    .replace("[name]", this.getName()));
        }
        if (status.hasDatabaseDisabled()) MessageManager.log(ConfigMessages.getDatabaseDisabled());
        if (status.hasDatabaseEmpty()) MessageManager.log(ConfigMessages.getDatabaseEmpty());
        if (status.hasPlayerCommand()) MessageManager.log(ConfigMessages.getPlayerCommand());
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

        // If the command runner is not a player return empty suggestions.
        if (!(source instanceof Player)) return CompletableFuture.completedFuture(List.of());

        // Get the user
        User user = new User((Player) source);

        // Get the argument index. Example: [0, 1, 2...]
        int index = invocation.arguments().length - 1;
        if (index == -1) index = 0;

        // Get this commands suggestions.
        CommandSuggestions suggestions = this.getSuggestions(this.getSection(), new User((Player) source));
        if (suggestions == null) suggestions = new CommandSuggestions();

        // Add sub command types.
        suggestions.appendSubCommandTypes(this.commandType.getSubCommandTypes(), this.getSection(), invocation.arguments(), user);

        // Check if there are no suggestions.
        if (suggestions.get() == null) return CompletableFuture.completedFuture(List.of());
        if (suggestions.get().isEmpty()) return CompletableFuture.completedFuture(List.of());
        if (suggestions.get().size() <= index) return CompletableFuture.completedFuture(List.of());

        // Get the current suggestions as a list.
        List<String> currentSuggestions = suggestions.get().get(index);
        if (currentSuggestions == null) return CompletableFuture.completedFuture(List.of());

        // If there are no arguments.
        if (invocation.arguments().length == 0) {
            return CompletableFuture.completedFuture(currentSuggestions);
        }

        // Get the current argument.
        String currentArgument = invocation.arguments()[index].trim();

        if (currentArgument.equals("")) {
            return CompletableFuture.completedFuture(currentSuggestions);
        }

        // Add items that contain the current argument to a list
        List<String> parsedList = new ArrayList<>();
        for (String item : currentSuggestions) {
            if (!item.toLowerCase(Locale.ROOT).contains(currentArgument)) continue;
            parsedList.add(item);
        }

        return CompletableFuture.completedFuture(parsedList);
    }
}
