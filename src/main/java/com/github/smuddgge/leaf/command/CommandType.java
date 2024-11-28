package com.github.smuddgge.leaf.command;

import com.github.smuddgge.leaf.user.ConsoleUser;
import com.github.smuddgge.leaf.user.PlayerUser;
import com.github.smuddgge.leaf.user.User;
import com.github.squishylib.configuration.ConfigurationSection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A command type defines a command's functionality.
 * <p>
 * Admins can then write configuration and apply the
 * functionality to the configuration sections.
 */
public interface CommandType {

    /**
     * The name of the command type.
     * <p>
     * For example, "info" for the type of command that
     * responds with a message.
     *
     * @return The command type's name.
     */
    @NotNull String getName();

    /**
     * The command's syntax.
     * <pre>
     * [] Is a required argument.
     * <> Is a optional argument.
     *
     * [name] Will be replaced with the commands name.
     * [parent] Will be replaced with the parent command
     *          name if it's a subcommand.
     *
     * Example: "/[parent] [name] [player]"
     *
     * @return The command's syntax.
     */
    @NotNull String getSyntax();

    /**
     * When a player starts writing the command they have the option
     * of tab completing to write the command quicker.
     * <p>
     * The {@link CommandSuggestions} class can be used to provide
     * those tab completions.
     *
     * @param section The command type's configuration section.
     * @param user    The player that is tab completing.
     * @return The command's argument suggestions.
     */
    @Nullable CommandSuggestions getSuggestions(ConfigurationSection section, PlayerUser user);

    @NotNull CommandStatus onUser(@NotNull ConfigurationSection section, @NotNull User user);

    @NotNull CommandStatus onPlayer(@NotNull ConfigurationSection section, @NotNull PlayerUser user);

    @NotNull CommandStatus onConsole(@NotNull ConfigurationSection section, @NotNull ConsoleUser user);

    /**
     * Executed when a command is registered.
     *
     * @param section The command section.
     * @param action  The create command action to add options.
     */
    default void onDiscordRegister(ConfigurationSection section, @NotNull CommandCreateAction action) {
    }

    /**
     * Executed when a member executes the command in discord.
     * Default as it is not required in a command.
     *
     * @param section The instance of the configuration section.
     * @param event   The instance of the slash event.
     * @return The instance of the command's status.
     */
    default CommandStatus onDiscordRun(ConfigurationSection section, SlashCommandInteractionEvent event) {
        return null;
    }

    /**
     * Executed when a member on a discord server sends a message.
     *
     * @param section The instance of the configuration section.
     * @param event   The instance of the event.
     */
    default void onDiscordMessage(ConfigurationSection section, @NotNull MessageReceivedEvent event) {

    }
}
