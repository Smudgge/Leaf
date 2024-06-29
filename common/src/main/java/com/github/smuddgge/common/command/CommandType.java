package com.github.smuddgge.common.command;

import com.github.smuddgge.common.user.PlayerUser;
import com.github.smuddgge.common.user.User;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;

public interface CommandType {

    /**
     * This will be used to identify command's
     * in the configuration files.
     *
     * @return The command type's identifier.
     */
    String getIdentifier();

    /**
     * Used to get the command's syntax.
     * <li>[parent] will be replaced with the sub commands before this one.</li>
     * <li>[name] will be replaced with the command's name. This will be provided in the configuration.</li>
     *
     * @return The command's syntax.
     */
    String getSyntax();

    /**
     * Used to get the tab suggestions.
     *
     * @param section The configuration section.
     * @param user    The user completing the command.
     * @return The command's argument suggestions.
     */
    CommandSuggestions getSuggestions(ConfigurationSection section, User user);

    /**
     * Executed when the command is run in the console.
     *
     * @param section   The command's configuration section.
     * @param arguments The arguments given in the command.
     * @param user The console user.
     * @return The command's status.
     */
    CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments, User user);

    /**
     * Executed when a player runs the command.
     *
     * @param section   The command's configuration section.
     * @param arguments The arguments given in the command.
     * @param user      The instance of the user running the command.
     * @return The command's status.
     */
    CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, PlayerUser user);

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
     * @return The instance of the commands status.
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
