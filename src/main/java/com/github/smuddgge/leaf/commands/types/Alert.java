package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigurationKey;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.dependencys.ProtocolizeDependency;
import com.github.smuddgge.leaf.discord.DiscordWebhookAdapter;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.utility.Sounds;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.velocitypowered.api.proxy.Player;

/**
 * <h1>Alert Command Type</h1>
 * Used to alert all online players with a message.
 */
public class Alert extends BaseCommandType {

    @Override
    public String getName() {
        return "alert";
    }

    @Override
    public String getSyntax() {
        return "/[name] [message]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        // Get the message.
        String message = section.getAdaptedString("message", "\n")
                .replace("%message%", String.join(" ", arguments));

        // Send the message to all online players.
        for (Player player : Leaf.getServer().getAllPlayers()) {
            new User(player).sendMessage(message);

            // Play sound if it exists.
            if (ProtocolizeDependency.isEnabled()) Sounds.play(section.getString("see_sound", null), player.getUniqueId());
        }

        // Check if there is a discord webhook.
        if (section.getKeys().contains(ConfigurationKey.DISCORD_WEBHOOK.getKey())) {
            DiscordWebhookAdapter adapter = new DiscordWebhookAdapter(
                    section.getSection(ConfigurationKey.DISCORD_WEBHOOK.getKey())
            );

            adapter.setPlaceholderParser(string -> PlaceholderManager.parse(
                    string.replace("%message%", String.join(" ", arguments)),
                    null, new User(null, "Console")));

            adapter.send();
        }

        // Log the message in console.
        MessageManager.log(message);
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {// Check if there is a discord webhook.
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        // Get the message.
        String message = section.getAdaptedString("message", "\n")
                .replace("%message%", String.join(" ", arguments));

        // Send the message to all online players.
        for (Player player : Leaf.getServer().getAllPlayers()) {
            new User(player).sendMessage(message);

            // Play sound if it exists.
            if (ProtocolizeDependency.isEnabled()) Sounds.play(section.getString("see_sound", null), player.getUniqueId());
        }

        // Log the message in console.
        MessageManager.log(message);

        // Check for discord webhook.
        if (section.getKeys().contains(ConfigurationKey.DISCORD_WEBHOOK.getKey())) {
            DiscordWebhookAdapter adapter = new DiscordWebhookAdapter(
                    section.getSection(ConfigurationKey.DISCORD_WEBHOOK.getKey())
            );

            adapter.setPlaceholderParser(string -> PlaceholderManager.parse(
                    string.replace("%message%", String.join(" ", arguments)),
                    null, user));

            adapter.send();
        }

        return new CommandStatus();
    }
}
