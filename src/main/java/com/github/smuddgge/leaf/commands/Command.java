package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.configuration.ConfigurationManager;
import com.github.smuddgge.leaf.database.tables.CommandLimitTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.dependencys.ProtocolizeDependency;
import com.github.smuddgge.leaf.utility.Sounds;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.data.Sound;
import org.jetbrains.annotations.NotNull;

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
        return this.getSection().getString("syntax", this.commandType.getSyntax());
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
     * This will also check for permissions.
     *
     * @param arguments The arguments given in the command.
     * @param user      The instance of the user running the command.
     * @return The command's status.
     */
    public CommandStatus onPlayerRun(String[] arguments, User user) {

        // Double check permissions.
        if (!user.hasPermission(this.getPermission())) return new CommandStatus().noPermission();

        // Check for permission-based requirements.
        if (this.getSection().getKeys().contains("require")) {
            ConfigurationSection requireSection = this.getSection().getSection("require");

            // For each requirement.
            for (String identifier : requireSection.getKeys()) {
                // Get the permission.
                String permission = requireSection.getSection(identifier).getString("permission", null);
                if (permission == null) continue;

                // Get a server list.
                List<String> serverList = requireSection.getSection(identifier).getListString("servers", new ArrayList<>());
                if (serverList.size() <= 0) continue;

                boolean userOnServer = serverList.contains(user.getConnectedServer().getServerInfo().getName());
                boolean hasPermission = user.hasPermission(permission);

                if (userOnServer && !hasPermission) {
                    return new CommandStatus().noPermission();
                }
            }
        }

        // Check the command limit.
        if (this.isLimited(user)) return new CommandStatus().isLimited();

        // Play sound.
        if (this.getSound() != null || Objects.equals(this.getSound(), "")) {
            try {
                if (ProtocolizeDependency.isEnabled()) Sounds.play(this.getSound(), user.getUniqueId());
            } catch (IllegalArgumentException illegalArgumentException) {
                MessageManager.warn("Invalid sound for command " + this.getName() + " : ");
                illegalArgumentException.printStackTrace();
            }
        }

        // Check if there are no sub command types.
        if (this.commandType.getSubCommandTypes().isEmpty()
                || arguments.length <= 0)
            return this.commandType.onPlayerRun(this.getSection(), arguments, user).increaseExecutions(user, this);

        // Otherwise, check if it is a sub command.
        for (CommandType commandType : this.commandType.getSubCommandTypes()) {
            String name = arguments[0];

            List<String> subCommandNames = new ArrayList<>();
            subCommandNames.add(this.getSection().getSection(commandType.getName()).getString("name", commandType.getName()));
            subCommandNames.addAll(this.getSection().getSection(commandType.getName()).getListString("aliases", new ArrayList<>()));

            if (subCommandNames.contains(name))
                return commandType.onPlayerRun(this.getSection(), arguments, user).increaseExecutions(user, this);
        }

        return this.commandType.onPlayerRun(this.getSection(), arguments, user).increaseExecutions(user, this);
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
        return ConfigurationManager.getCommands().getCommand(this.identifier);
    }

    /**
     * Used to get the name of the command.
     *
     * @return The name of the command.
     */
    public String getName() {
        return ConfigurationManager.getCommands().getCommandName(this.identifier);
    }

    /**
     * Used to get the command's description.
     * This will be used for discord commands.
     *
     * @return The command's description.
     */
    public String getDescription() {
        return ConfigurationManager.getCommands().getCommand(this.identifier).getString("description", "No Description");
    }

    /**
     * Used to get the command's aliases.
     * These are other command names that will execute this command.
     *
     * @return The list of aliases.
     */
    public CommandAliases getAliases() {
        return ConfigurationManager.getCommands().getCommandAliases(this.identifier);
    }

    /**
     * Used to get the permission to execute the command.
     *
     * @return Command permission.
     */
    public String getPermission() {
        return ConfigurationManager.getCommands().getCommandPermission(this.identifier);
    }

    /**
     * Used to get the sound played when the command is executed.
     *
     * @return The sound to play as a string.
     */
    public String getSound() {
        return ConfigurationManager.getCommands().getCommandSound(this.identifier);
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
        return ConfigurationManager.getCommands().isCommandEnabled(this.identifier);
    }

    /**
     * Used to check if the command is executable from discord.
     *
     * @return True if this command can be executed on discord.
     */
    public boolean isDiscordEnabled() {
        return ConfigurationManager.getCommands().getCommand(this.identifier).getBoolean("discord_bot.enabled", false);
    }

    /**
     * Used to check if a user has exceeded the limit
     * on using this command.
     *
     * @param user The instance of the user.
     * @return True if the user has reached the limit.
     */
    public boolean isLimited(@NotNull User user) {

        // Get the command limit for this command.
        int limit = ConfigurationManager.getCommands().getCommandLimit(this.identifier);

        // Check if there is no limit.
        if (limit == -1) return false;

        // Check if the database is disabled.
        // We return true because the database may have disabled its self
        // and the admin may still want commands to be limited.
        if (Leaf.isDatabaseDisabled()) return true;

        int amountExecuted = Leaf.getDatabase()
                .getTable(CommandLimitTable.class)
                .getAmountExecuted(user.getUniqueId(), this.identifier);

        // Check if the amount of times the command
        // has been executed is bigger or equal to the limit.
        return amountExecuted >= limit;
    }

    /**
     * Used to check if the command has a limit.
     *
     * @return True if teh command has a limit.
     */
    public boolean hasLimit() {

        // Get the command limit for this command.
        int limit = ConfigurationManager.getCommands().getCommandLimit(this.identifier);

        // Check if there is no limit.
        return limit != -1;
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

                String message = status.getMessage();
                if (message == null) return;
                user.sendMessage(message);

                return;
            } catch (Exception exception) {
                user.sendMessage(ConfigMessages.getError());
                MessageManager.warn("Error occurred while running command : " + this.getName());
                exception.printStackTrace();
                return;
            }
        }

        try {
            // Run the command in console.
            CommandStatus status = this.onConsoleRun(invocation.arguments());
            if (status.hasIncorrectArguments()) {
                MessageManager.log(ConfigMessages.getIncorrectArguments(this.getSyntax())
                        .replace("[name]", this.getName()));
            }

            String message = status.getMessage();
            if (message == null) return;
            MessageManager.log(message);

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
        if (suggestions.get().size() <= index) {
            // If continuous return the last suggestions.
            if (suggestions.isContinuous()) {
                return CompletableFuture.completedFuture(
                        suggestions.get().get(suggestions.get().size() - 1)
                );
            }

            return CompletableFuture.completedFuture(List.of());
        }

        // Get the current suggestions as a list.
        List<String> currentSuggestions = suggestions.get().get(index);
        if (currentSuggestions == null) {

            // If continuous return the last suggestions.
            if (suggestions.isContinuous()) {
                return CompletableFuture.completedFuture(
                        suggestions.get().get(suggestions.get().size() - 1)
                );
            }

            return CompletableFuture.completedFuture(List.of());
        }

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
