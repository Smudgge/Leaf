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
        // Get message.
        String message = section.getAdaptedString("message", "\n", "");

        // Send the message.
        if (!message.isEmpty()) {

            // Send the message to all online players.
            for (Player player : Leaf.getServer().getAllPlayers()) {
                new User(player).sendMessage(message);

                // Play sound if it exists.
                if (ProtocolizeDependency.isEnabled()) Sounds.play(section.getString("see_sound", null), player.getUniqueId());
            }
        }

        // Check if there is a discord webhook.
        if (section.getKeys().contains(ConfigurationKey.DISCORD_WEBHOOK.getKey())) {
            DiscordWebhookAdapter adapter = new DiscordWebhookAdapter(
                    section.getSection(ConfigurationKey.DISCORD_WEBHOOK.getKey())
            );

            adapter.setPlaceholderParser(string -> PlaceholderManager.parse(
                    string.replace("%message%", message),
                    null, user));

            adapter.send();
        }

        // Log the message.
        MessageManager.log(PlaceholderManager.parse(message, null, user));
        return new CommandStatus();
    }
}
