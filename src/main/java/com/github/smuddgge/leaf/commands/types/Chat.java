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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <h1>Chat Command Type</h1>
 * Used to send a message in a chat.
 * This chat is for select players.
 */
public class Chat extends BaseCommandType {

    @Override
    public String getName() {
        return "chat";
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
        String rawMessage = section.getAdaptedString("format", "\n")
                .replace("%message%", String.join(" ", arguments));

        // Parse the message.
        String message = PlaceholderManager.parse(rawMessage, null, new User(null, "Console"));

        // Get the permission.
        String permission = section.getString("permission");

        // Send the message.
        Chat.sendToPlayers(section, permission, message);

        // Check if a discord webhook message should be sent.
        if (section.getKeys().contains(ConfigurationKey.DISCORD_WEBHOOK.getKey())) {
            DiscordWebhookAdapter adapter = new DiscordWebhookAdapter(
                    section.getSection(ConfigurationKey.DISCORD_WEBHOOK.getKey())
            );

            // Parse placeholders.
            adapter.setPlaceholderParser(string -> PlaceholderManager.parse(
                    string.replace("%message%", String.join(" ", arguments)),
                    null,
                    new User(null, "Console")));

            // Send to discord.
            adapter.send();
        }

        // Log the message.
        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        // Get the message.
        String rawMessage = section.getAdaptedString("format", "\n")
                .replace("%message%", String.join(" ", arguments));

        // Parse the message.
        String message = PlaceholderManager.parse(rawMessage, null, user);

        // Get the permission.
        String permission = section.getString("permission");

        // Send the message.
        Chat.sendToPlayers(section, permission, message);

        // Check if a discord webhook message should be sent.
        if (section.getKeys().contains(ConfigurationKey.DISCORD_WEBHOOK.getKey())) {
            DiscordWebhookAdapter adapter = new DiscordWebhookAdapter(
                    section.getSection(ConfigurationKey.DISCORD_WEBHOOK.getKey())
            );

            // Parse placeholders.
            adapter.setPlaceholderParser(string -> PlaceholderManager.parse(
                    string.replace("%message%", String.join(" ", arguments)),
                    null, user));

            // Send to discord.
            adapter.send();
        }

        // Log the message if it is enabled.
        if (section.getBoolean("log", true)) MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public void onDiscordMessage(ConfigurationSection section, @NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String name = event.getAuthor().getName();

        String formatted = PlaceholderManager.parse(
                        section.getAdaptedString("discord_bot.format", "\n", "&b&lDiscord&r &f%name%&r &7: &f%message%"),
                        null, null
                )
                .replace("%message%", message)
                .replace("%name%", name);

        Chat.sendToPlayers(section, section.getString("permission"), formatted);

        // Log the message if it is enabled.
        if (section.getBoolean("log", true)) MessageManager.log(formatted);
    }

    /**
     * Used to send the message to players.
     *
     * @param section    The instance of the configuration section.
     * @param permission The permission to filter.
     * @param message    The message to send.
     */
    public static void sendToPlayers(@NotNull ConfigurationSection section,
                                     @Nullable String permission,
                                     @NotNull String message) {

        // Send to players with the permission.
        for (Player player : Leaf.getServer().getAllPlayers()) {

            // Check if there is a permission
            // and if the player does not have the permission.
            if (permission != null
                    && !player.hasPermission(section.getString("permission"))) continue;

            User toSend = new User(player);
            toSend.sendMessage(message);
            if (ProtocolizeDependency.isEnabled()) Sounds.play(section.getString("receive_sound"), toSend.getUniqueId());
        }
    }
}
