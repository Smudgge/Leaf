package com.github.smuddgge.leaf.command;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.LeafException;
import com.github.smuddgge.leaf.database.CommandCooldownRecord;
import com.github.smuddgge.leaf.database.CommandCooldownTable;
import com.github.smuddgge.leaf.database.CommandLimitRecord;
import com.github.smuddgge.leaf.database.CommandLimitTable;
import com.github.smuddgge.leaf.logger.Logger;
import com.github.smuddgge.leaf.user.ConsoleUser;
import com.github.smuddgge.leaf.user.PlayerUser;
import com.github.smuddgge.leaf.user.User;
import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.database.Query;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a command within this plugin.
 * <p>
 * Please note this implementation only allows for 1
 * subcommand layer per command. The configuration file
 * would get quite messy if there were more layers anyway.
 * <pre>
 * /name
 * /name arguments
 * /name layer1 arguments
 */
public class Command implements SimpleCommand {

    private final @NotNull String identifier;
    private final @NotNull BaseCommandType commandType;

    /**
     * Creates a new instance of a leaf command.
     *
     * @param identifier  The command's identifier.
     *                    This is the key of the configuration section.
     * @param commandType The command's type.
     */
    public Command(@NotNull String identifier, @NotNull BaseCommandType commandType) {
        this.identifier = identifier;
        this.commandType = commandType;
    }

    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    public @NotNull BaseCommandType getCommandType() {
        return this.commandType;
    }

    public @NotNull ConfigurationSection getSection() {
        return Leaf.get().getCommandsDirectory().getSection(this.identifier);
    }

    public @NotNull String getName() {
        final String name = this.getSection().getString("name", null);
        if (name != null) return name;

        Leaf.get().getLogger().warn(
                "The command with identifier {identifier} does not have a name! Please add its name to the command configuration directory. For now the name will be /null-command."
                        .replace("{identifier}", this.identifier)
        );
        return "null-command";
    }

    public @NotNull List<String> getAliases() {
        return this.getSection().getListString("aliases", new ArrayList<>());
    }

    public @NotNull String getDescription() {
        return this.getSection().getString("description", "No Description");
    }

    public @NotNull String getDefaultSyntax() {
        return this.commandType.getSyntax()
                .replace("[name]", this.getName());
    }

    public @NotNull String getSyntax() {
        return this.getSection().getString("syntax", this.getDefaultSyntax())
                .replace("[name]", this.getName());
    }

    public @Nullable String getPermission() {
        return this.getSection().getString("permission", null);
    }

    public boolean isEnabled() {
        return this.getSection().getBoolean("enabled", true);
    }

    public boolean isDisabled() {
        return !this.isEnabled();
    }

    public boolean isDiscordEnabled() {
        if (this.getSection().getSection("discord_bot").getKeys().isEmpty()) return false;
        return this.getSection().getBoolean("discord_bot.enabled", true);
    }

    public int getLimit() {
        return this.getSection().getInteger("limit", -1);
    }

    public long getCooldown() {
        return this.getSection().getLong("cooldown", -1);
    }

    public boolean hasLimit() {
        return this.getLimit() != -1;
    }

    public boolean hasCooldown() {
        return this.getCooldown() != -1L;
    }

    public boolean isLimited(@NotNull User user) {

        if (!(user instanceof PlayerUser)) return false;

        // Get the command limit for this command.
        int limit = this.getLimit();

        // Check if there is no limit.
        if (limit == -1) return false;

        // Check if the database is disabled.
        // We return true because the database may have disabled its self
        // and the admin may still want commands to be limited.
        // -> If the admin is not using a database, they will set the limit to -1.
        if (Leaf.get().isDatabaseDisabled()) return true;

        // Get the users command limit record.
        final CommandLimitRecord record = Leaf.get().getDatabase()
                .getTable(CommandLimitTable.class)
                .getFirstRecord(new Query().match(
                        CommandLimitRecord.ID_FIELD,
                        CommandLimitRecord.createId(user.getUuid(), this.identifier)
                ))
                .waitAndGet();

        // Is the user not limited?
        if (record == null) return false;

        // Check if the amount of times the command
        // has been executed is bigger or equal to the limit.
        return record.getAmountExecuted() >= limit;
    }

    private boolean isOnCooldown(@NotNull User user) {

        if (!(user instanceof PlayerUser)) return false;

        long cooldown = this.getCooldown();
        if (cooldown == -1) return false;

        // Check if the database is disabled.
        // We return true because the database may have disabled its self
        // and the admin may still want commands to be limited.
        // -> If the admin is not using a database, they will set the cooldown to -1.
        if (Leaf.get().isDatabaseDisabled()) return true;

        final CommandCooldownRecord record = Leaf.get().getDatabase()
                .getTable(CommandCooldownTable.class)
                .getFirstRecord(new Query().match(CommandCooldownRecord.ID_FIELD, CommandCooldownRecord.createId(user.getUuid(), this.identifier)))
                .waitAndGet();

        // Is the user not on a cooldown?
        if (record == null) return false;

        // Is the user on a cooldown right now?
        return (record.getLastExecutedTimestamp() + cooldown) > System.currentTimeMillis();
    }

    /**
     * Returns the amount of time the user will have to wait until
     * they can execute the command again.
     *
     * @param user The instance of the user.
     * @return The duration until they can execute the command.
     */
    public @NotNull Duration getCooldownDurationLeft(@NotNull User user) {

        if (!(user instanceof PlayerUser)) return Duration.ofSeconds(0);

        // Get the command's cooldown.
        long cooldown = this.getCooldown();

        // Is there no cooldown?
        if (cooldown == -1) return Duration.ofSeconds(0);

        final CommandCooldownRecord record = Leaf.get().getDatabase()
                .getTable(CommandCooldownTable.class)
                .getFirstRecord(new Query().match(CommandCooldownRecord.ID_FIELD, CommandCooldownRecord.createId(user.getUuid(), this.identifier)))
                .waitAndGet();

        // Does the user have no cooldown?
        if (record == null) return Duration.ofSeconds(0);

        return Duration.ofMillis((record.getLastExecutedTimestamp() + cooldown) - System.currentTimeMillis());
    }

    private @NotNull CommandStatus onConsoleRun(@NotNull String[] arguments) {

        final boolean noSubCommands = this.commandType.getSubCommandTypes().isEmpty();
        final boolean noArguments = arguments.length == 0;

        // If there's no sub commands or if there's no arguments.
        if (noSubCommands || noArguments) {
            return new CommandStatus()
                    .merge(this.commandType.onUser(this.getSection(), new ConsoleUser(), arguments))
                    .merge(this.commandType.onConsole(this.getSection(), new ConsoleUser(), arguments));
        }

        // For each sub command.
        // Only supports one subcommand.
        for (CommandType commandType : this.commandType.getSubCommandTypes()) {
            String name = arguments[0];

            // Get the sub command section.
            ConfigurationSection subCommandSection = this.getSection().getSection(commandType.getIdentifier());

            // Get the list of all the command names.
            List<String> subCommandNames = new ArrayList<>();
            subCommandNames.add(subCommandSection.getString("name", commandType.getIdentifier()));
            subCommandNames.addAll(subCommandSection.getListString("aliases", new ArrayList<>()));

            if (subCommandNames.contains(name)) {
                return new CommandStatus()
                        .merge(commandType.onUser(this.getSection(), new ConsoleUser(), arguments))
                        .merge(commandType.onConsole(this.getSection(), new ConsoleUser(), arguments));
            }
        }

        return new CommandStatus()
                .merge(this.commandType.onUser(this.getSection(), new ConsoleUser(), arguments))
                .merge(this.commandType.onConsole(this.getSection(), new ConsoleUser(), arguments));
    }

    /**
     * This will also check if they have permission to run the command.
     *
     * @param arguments The arguments given in the command.
     * @param user      The instance of the user running the command.
     * @return The command's status.
     */
    private @NotNull CommandStatus onPlayerRun(@NotNull String[] arguments, @NotNull PlayerUser user, String alias) {

        Leaf.get().getLogger().optional(Logger.Opt.COMMAND_RUN, "[Commands] " + user.getName() + " ran /" + alias + " " + String.join(" ", arguments));

        final boolean permissionExists = this.getPermission() != null;
        final boolean hasPermission = user.hasPermission(this.getPermission() == null ? "" : this.getPermission());

        // Does the player have permission to run the base command?
        if (permissionExists && hasPermission) return new CommandStatus().set(CommandStatus.Status.NO_PERMISSION);

        // Check for permission-based requirements.
        if (this.getSection().getKeys().contains("require")) {

            // Get the requirement configuration section.
            final ConfigurationSection requireSection = this.getSection().getSection("require");

            // For each requirement.
            for (String identifier : requireSection.getKeys()) {

                // Get the permission.
                final String permission = requireSection.getSection(identifier).getString("permission", null);
                if (permission == null) continue;

                // Get a server list.
                List<String> serverList = requireSection.getSection(identifier).getListString("servers", new ArrayList<>());
                if (serverList.isEmpty()) continue;

                boolean userOnServer = serverList.contains(user.getServerName());
                boolean noPermission = user.hasPermission(permission);

                if (userOnServer && noPermission) {
                    return new CommandStatus().set(CommandStatus.Status.NO_PERMISSION);
                }
            }
        }

        // Check the command limit.
        if (this.isLimited(user)) return new CommandStatus().set(CommandStatus.Status.IS_LIMITED);

        // Check the command cooldown.
        if (this.isOnCooldown(user)) return new CommandStatus().set(CommandStatus.Status.IS_ON_COOLDOWN);

        final boolean noSubCommands = this.commandType.getSubCommandTypes().isEmpty();
        final boolean noArguments = arguments.length == 0;

        // If there's no sub commands or if there's no arguments.
        if (noSubCommands || noArguments) return new CommandStatus()
                .merge(this.commandType.onUser(this.getSection(), user, arguments))
                .merge(this.commandType.onPlayer(this.getSection(), user, arguments))
                .increaseExecutions(user, this)
                .applyCooldownIfNeeded(user, this);

        // Otherwise, check if it is a sub command.
        for (CommandType commandType : this.commandType.getSubCommandTypes()) {
            String name = arguments[0];

            List<String> subCommandNames = new ArrayList<>();
            subCommandNames.add(this.getSection().getSection(commandType.getIdentifier()).getString("name", commandType.getIdentifier()));
            subCommandNames.addAll(this.getSection().getSection(commandType.getIdentifier()).getListString("aliases", new ArrayList<>()));

            if (subCommandNames.contains(name)) return new CommandStatus()
                    .merge(commandType.onUser(this.getSection(), user, arguments))
                    .merge(commandType.onPlayer(this.getSection(), user, arguments))
                    .increaseExecutions(user, this)
                    .applyCooldownIfNeeded(user, this);
        }

        return new CommandStatus()
                .merge(this.commandType.onUser(this.getSection(), user, arguments))
                .merge(this.commandType.onPlayer(this.getSection(), user, arguments))
                .increaseExecutions(user, this)
                .applyCooldownIfNeeded(user, this);
    }

    @Override
    public void execute(final Invocation invocation) {
        try {

            final CommandSource source = invocation.source();

            if (source instanceof Player player) {
                this.execute(invocation.arguments(), player, invocation.alias());
                return;
            }

            // Run the command in console.
            CommandStatus status = this.onConsoleRun(invocation.arguments());

            // Get the status message.
            final String message = status.getFirstMessage();
            if (message == null) return;

            // Send the message to console.
            Leaf.get().getLogger().info(message);

        } catch (Exception exception) {
            throw new LeafException(exception, "Command.execute(invocation)", "Error occurred while executing command &c" + invocation.alias() + "&f.", null);
        }
    }

    public void execute(@NotNull String[] arguments, @NotNull Player player, String alias) {

        // Create a player user.
        final PlayerUser user = new PlayerUser(player);

        // Run the command as a player.
        final CommandStatus status = this.onPlayerRun(arguments, user, alias);

        // Get the status message.
        final String message = status.getFirstMessage();
        if (message == null) return;

        // Send the message to the player.
        user.sendMessage(message.replace("%command%", this.getSyntax()));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        String permission = this.getPermission();

        if (permission == null) return true;

        return invocation.source().hasPermission(this.getPermission());
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {

        final CommandSource source = invocation.source();

        // If the command runner is not a player return empty suggestions.
        if (!(source instanceof Player player)) return CompletableFuture.completedFuture(List.of());

        // Get the player as a user.
        PlayerUser user = new PlayerUser(player);

        // Get the argument index. Example: [0, 1, 2...]
        int index = invocation.arguments().length - 1;
        if (index == -1) index = 0;

        // Get this commands suggestions.
        CommandSuggestions suggestions = this.commandType.getSuggestions(this.getSection(), user);
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
