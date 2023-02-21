package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.utility.Sounds;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.data.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * <h1>Represents a custom command.</h1>
 *
 * @param identifier  The command's identifier in the configuration.
 * @param commandType The base command type it will use.
 */
public record Command(String identifier,
                      BaseCommandType commandType) implements SimpleCommand {

    /**
     * Used to get the command's syntax.
     * This is to get the raw syntax where the placeholders
     * have not been parsed.
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
        // Check if there are no subcommands or there are no arguments.
        if (this.commandType.getSubCommandTypes().isEmpty()
                || arguments.length == 0) return this.commandType.onConsoleRun(this.getSection(), arguments);

        // For each sub command.
        for (CommandType commandType : this.commandType.getSubCommandTypes()) {
            String name = arguments[0];

            // Get the sub command section.
            ConfigurationSection subcommandSection = this.getSection().getSection(commandType.getName());

            // Get the list of all the command names.
            List<String> subCommandNames = new ArrayList<>();
            subCommandNames.add(subcommandSection.getString("name", commandType.getName()));
            subCommandNames.addAll(subcommandSection.getListString("aliases", new ArrayList<>()));

            if (subCommandNames.contains(name)) return commandType.onConsoleRun(this.getSection(), arguments);
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

        // Play sound.
        if (this.getSound() != null || Objects.equals(this.getSound(), "")) {
            try {
                Sounds.play(Sound.valueOf(this.getSound().toUpperCase(Locale.ROOT)), user.getUniqueId());
            } catch (IllegalArgumentException illegalArgumentException) {
                MessageManager.warn("Invalid sound for command " + this.getName() + " : ");
                illegalArgumentException.printStackTrace();
            }
        }

        if (this.commandType.getSubCommandTypes().isEmpty()
                || arguments.length <= 0) return this.commandType.onPlayerRun(this.getSection(), arguments, user);

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
     * Used to get the sound played when the command is executed.
     *
     * @return The sound to play as a string.
     */
    public String getSound() {
        return ConfigCommands.getCommandSound(this.getIdentifier());
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

            try {
                // Run the command as a player.
                CommandStatus status = this.onPlayerRun(invocation.arguments(), user);

                if (status.hasIncorrectArguments()) {
                    user.sendMessage(ConfigMessages.getIncorrectArguments(this.getSyntax())
                            .replace("[name]", this.getName()));
                }

                if (status.hasError()) user.sendMessage(ConfigMessages.getError());
                if (status.hasDatabaseDisabled()) user.sendMessage(ConfigMessages.getDatabaseDisabled());
                if (status.hasDatabaseEmpty()) user.sendMessage(ConfigMessages.getDatabaseEmpty());
                if (status.hasPlayerCommand()) user.sendMessage(ConfigMessages.getPlayerCommand());

                return;
            } catch (Exception exception) {
                user.sendMessage(ConfigMessages.getError());
                MessageManager.warn("Error occurred while running command : " + this.getName());
                exception.printStackTrace();
            }
        }


        try {
            // Run the command in console.
            CommandStatus status = this.onConsoleRun(invocation.arguments());

            if (status.hasIncorrectArguments()) {
                MessageManager.log(ConfigMessages.getIncorrectArguments(this.getSyntax())
                        .replace("[name]", this.getName()));
            }

            if (status.hasError()) MessageManager.log(ConfigMessages.getError());
            if (status.hasDatabaseDisabled()) MessageManager.log(ConfigMessages.getDatabaseDisabled());
            if (status.hasDatabaseEmpty()) MessageManager.log(ConfigMessages.getDatabaseEmpty());
            if (status.hasPlayerCommand()) MessageManager.log(ConfigMessages.getPlayerCommand());

        } catch (Exception exception) {
            MessageManager.warn("Error occurred while running command : " + this.getName());
            exception.printStackTrace();
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
