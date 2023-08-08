package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigurationKey;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.discord.DiscordWebhookAdapter;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Alert Message Command Type</h1>
 * Used to alert all online players with a pre written message.
 */
public class AlertMessage extends BaseCommandType {

    @Override
    public String getName() {
        return "alertmessage";
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
        return this.onPlayerRun(section, arguments, new User(null, "Console"));
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        // Get a message as a list.
        // If the message is not a list, it will return an empty list.
        List<String> listMessage = section.getListString("message", new ArrayList<>());

        // The final message to send.
        String message;

        // If size 0 assume it's a string.
        if (listMessage.isEmpty()) {
            message = section.getString("message", "");

        } else {
            // Otherwise, it's a list.
            StringBuilder builder = new StringBuilder();
            for (String string : listMessage) builder.append(string).append("\n");
            String toSend = builder.toString();

            // Get rid of the last '\n'.
            message = toSend.substring(0, toSend.length() - 1);
        }

        if (!message.isEmpty()) {

            // Send the message to all online players.
            for (Player player : Leaf.getServer().getAllPlayers()) {
                new User(player).sendMessage(message);
            }
        }

        // Check if there is a discord webhook.
        if (section.getKeys().contains(ConfigurationKey.DISCORD_WEBHOOK.getKey())) {
            DiscordWebhookAdapter adapter = new DiscordWebhookAdapter(
                    section.getSection(ConfigurationKey.DISCORD_WEBHOOK.getKey())
            );

            String finalMessage = message;
            adapter.setPlaceholderParser(string -> PlaceholderManager.parse(
                    string.replace("%message%", finalMessage),
                    null, user));

            adapter.send();
        }

        // Log the message.
        MessageManager.log(PlaceholderManager.parse(message, null, user));
        return new CommandStatus();
    }
}
