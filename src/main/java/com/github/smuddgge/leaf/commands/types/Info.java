package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.discord.DiscordBotMessageAdapter;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * <h1>Info Command Type</h1>
 * Used send information to players when executed.
 */
public class Info extends BaseCommandType {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {

        // Get a message as a list.
        // If the message is not a list, it will return null.
        String message = section.getAdaptedString("message", "\n", "");

        // Log the message.
        MessageManager.log(PlaceholderManager.parse(message, null, new User(null, "Console")));
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {

        // Get a message as a list.
        // If the message is not a list, it will return null.
        String message = section.getAdaptedString("message", "\n", "");

        // Send the message to the user.
        user.sendMessage(message);
        return new CommandStatus();
    }

    @Override
    public CommandStatus onDiscordRun(ConfigurationSection section, SlashCommandInteractionEvent event) {

        // Send the message.
        event.reply(new DiscordBotMessageAdapter(
                section, "discord_bot.message",
                "No message."
        ).buildMessage()).queue();

        return new CommandStatus();
    }
}
