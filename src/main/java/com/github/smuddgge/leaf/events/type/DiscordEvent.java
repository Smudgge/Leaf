package com.github.smuddgge.leaf.events.type;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.events.Event;
import com.github.smuddgge.leaf.events.EventType;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.console.Console;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a custom discord event.
 *
 * @param identifier The events unique identifier.
 * @param section    The instance of the identifier's section.
 * @param type       The type of event it will be listening to.
 */
public record DiscordEvent(@NotNull String identifier,
                           @NotNull ConfigurationSection section,
                           @NotNull EventType type) implements Event {

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public EventType getEventType() {
        return this.type;
    }

    @Override
    public void onEvent(@NotNull User user) {
    }

    @Override
    public void onDiscordMessage(@NotNull MessageReceivedEvent messageReceivedEvent) {

        // Check if it is the correct type.
        if (this.type != EventType.DISCORD_MESSAGE) return;

        // Check if the event is enabled.
        if (!this.section.getBoolean("enabled", true)) return;

        // Check if it's in the correct channel.
        if (this.section.getKeys().contains("allowed_channels")
                && !this.section.getListString("allowed_channels")
                .contains(messageReceivedEvent.getChannel().getId())) {

            return;
        }

        // Check if they have the correct permissions.
        if (this.section.getKeys().contains("permissions")
                && messageReceivedEvent.getMember() != null
                && !messageReceivedEvent.getMember().hasPermission(this.getPermissions())) {

            return;
        }

        // Get message.
        String message = section.getAdaptedString("message", "\n", null);
        if (message == null) return;

        // Replace placeholders.
        message = message
                .replace("%message%", messageReceivedEvent.getMessage().getContentRaw())
                .replace("%name%", messageReceivedEvent.getAuthor().getName());

        // Send message.
        for (String serverName : this.section.getListString("send_to")) {
            Optional<RegisteredServer> optionalRegisteredServer = Leaf.getServer().getServer(serverName);
            if (optionalRegisteredServer.isEmpty()) continue;
            RegisteredServer server = optionalRegisteredServer.get();

            // Loop though players.
            for (Player player : server.getPlayersConnected()) {
                new User(player).sendMessage(message);
            }
        }
    }

    /**
     * Used to get the list of discord permissions.
     *
     * @return The list of discord permissions.
     */
    public @NotNull List<Permission> getPermissions() {
        List<Permission> permissionList = new ArrayList<>();

        for (String permissionString : this.section.getListString("permissions", new ArrayList<>())) {
            try {
                permissionList.add(Permission.valueOf(permissionString.toUpperCase()));
            } catch (Exception exception) {
                Console.warn("Tried to get events discord permission list but " + permissionString + " doesnt exist.");
                exception.printStackTrace();
            }
        }

        return permissionList;
    }
}