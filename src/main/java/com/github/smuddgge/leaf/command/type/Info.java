package com.github.smuddgge.leaf.command.type;

import com.github.smuddgge.leaf.command.BaseCommandType;
import com.github.smuddgge.leaf.command.CommandStatus;
import com.github.smuddgge.leaf.command.CommandSuggestions;
import com.github.smuddgge.leaf.user.DiscordBotUser;
import com.github.smuddgge.leaf.user.PlayerUser;
import com.github.smuddgge.leaf.user.User;
import com.github.squishylib.configuration.ConfigurationSection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * info:
 *   type: "info"
 *   name: "test"
 *   message: "This is a command used for testing."
 *   pages:
 *     hi: "Hi!"
 *     hello: "Hello!"
 *   default: "This page doesn't exist."
 *   discord_bot:
 *     argument_name: "page"
 *     argument_description: "The info page to display."
 *     message: "This is a command used for testing."
 *     pages:
 *       hi: "Hi!"
 *       hello: "Hello!"
 */
public class Info extends BaseCommandType {

    @Override
    public @NotNull String getIdentifier() {
        return "info";
    }

    @Override
    public @NotNull String getSyntax() {
        return "/[name] <page>";
    }

    @Override
    public @Nullable CommandSuggestions getSuggestions(@NotNull ConfigurationSection section, @NotNull PlayerUser user) {
        return null;
    }

    @Override
    public @NotNull CommandStatus onUser(@NotNull ConfigurationSection section, @NotNull User user, @NotNull String[] arguments) {

        // Are their arguments?
        if (arguments.length >= 1) {

            // Get the message given the argument.
            String message = section.getSection("pages").getAdaptedString(
                arguments[0], "\n",
                section.getString("default", "The page &f%page% &7doesnt exist.")
                    .replace("%page%", arguments[0])
            );

            user.sendMessage(message);
            return new CommandStatus();
        }

        // Get the message to send back to the user.
        final String message = section.getAdaptedString("message", "\n", "&cMessage not defined.");

        // Send the message back to the user.
        user.sendMessage(message);
        return new CommandStatus();
    }

    @Override
    public void onDiscordRegister(@NotNull ConfigurationSection section, @NotNull CommandCreateAction action) {
        if (section.getString("discord_bot.argument_name") == null) return;

        action.addOption(
            OptionType.STRING,
            section.getString("discord_bot.argument_name", "page"),
            section.getString("discord_bot.argument_description", "")
        ).complete();
    }

    @Override
    public CommandStatus onDiscordRun(@NotNull ConfigurationSection section, @NotNull SlashCommandInteractionEvent event, @NotNull DiscordBotUser user) {

        // Get the argument name.
        final String argumentName = section.getString("discord_bot.argument_name");

        // Does the argument name exist?
        if (argumentName != null) {

            // Get the argument inputted by the user.
            final OptionMapping argumentMapping = event.getOption(argumentName);

            // Check if the argument exists.
            if (argumentMapping != null && !argumentMapping.getAsString().isEmpty()) {
                final String page = argumentMapping.getAsString();

                user.sendMessage(
                    section,
                    "discord_bot.pages" + argumentMapping.getAsString(),
                    section.getString("discord_bot.default", "The page %page% doesnt exist.")
                        .replace("%page%", page)
                );
            }
        }

        // Check if no arguments were defined.
        if (section.getString("discord_bot.argument_name") == null) {

            // Send the message.
            user.sendMessage(
                section,
                "discord_bot.message",
                "*Message not defined.*"
            );
        }

        return new CommandStatus();
    }
}
