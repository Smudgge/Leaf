package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.discord.DiscordBotMessageAdapter;
import com.github.smuddgge.leaf.discord.DiscordBotRemoveMessageHandler;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;

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
        return new CommandSuggestions().append(section.getSection("arguments").getKeys());
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {

        // If there are arguments.
        if (arguments.length >= 1) {
            String message = section.getSection("arguments").getAdaptedString(
                    arguments[0], "\n",
                    section.getString("alternative", "The message for this argument doesn't exist.")
            );

            MessageManager.log(PlaceholderManager.parse(message, null, new User(null, "Console")));
            return new CommandStatus();
        }

        // Get a message as a list.
        // If the message is not a list, it will return null.
        String message = section.getAdaptedString("message", "\n", "");

        // Log the message.
        MessageManager.log(PlaceholderManager.parse(message, null, new User(null, "Console")));
        return new CommandStatus();
    }

    /*
        arguments:
          case1:
          - "Message to sent when this argument is used"
         */
    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {

        // If there are arguments.
        if (arguments.length >= 1) {
            String message = section.getSection("arguments").getAdaptedString(
                    arguments[0], "\n",
                    section.getString("alternative", "The message for this argument doesn't exist.")
            );

            user.sendMessage(message);
            return new CommandStatus();
        }

        // Get a message as a list.
        // If the message is not a list, it will return null.
        String message = section.getAdaptedString("message", "\n", "");

        // Send the message to the user.
        user.sendMessage(message);
        return new CommandStatus();
    }

    @Override
    public void onDiscordRegister(ConfigurationSection section, @NotNull CommandCreateAction action) {
        action.addOption(
                OptionType.STRING,
                section.getString("discord_bot.argument_name", "Argument"),
                section.getString("discord_bot.argument_description", "")
        ).complete();
    }

    @Override
    public CommandStatus onDiscordRun(ConfigurationSection section, SlashCommandInteractionEvent event) {

        String argument = event.getOption(section.getString("discord_bot.argument_name", "Argument")).getAsString();
        if (!argument.isEmpty()) {
            event.reply(new DiscordBotMessageAdapter(
                    section, "discord_bot.arguments." + argument,
                    section.getString("discord_bot.alternative", "The message for this argument doesn't exist.")
            ).buildMessage()).complete();
            return new CommandStatus();
        }

        // Send the message.
        event.reply(new DiscordBotMessageAdapter(
                section, "discord_bot.message",
                "No message."
        ).buildMessage()).complete();
        new DiscordBotRemoveMessageHandler(section.getSection("discord_bot"), event);

        return new CommandStatus();
    }
}
