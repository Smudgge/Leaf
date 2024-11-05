package com.github.smuddgge.leaf.events.type;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configurationold.ConfigurationKey;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.discord.DiscordWebhookAdapter;
import com.github.smuddgge.leaf.events.Event;
import com.github.smuddgge.leaf.events.EventType;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Represents a custom standard event.
 *
 * @param identifier The events unique identifier.
 * @param section    The instance of the identifier's section.
 * @param type       The type of event it will be listening to.
 */
public record StandardEvent(@NotNull String identifier,
                            @NotNull ConfigurationSection section,
                            @NotNull EventType type) implements Event {

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public EventType getEventType() {
        EventType eventType = EventType.getFromType(this.section.getString("type"));

        if (eventType == null) {
            MessageManager.warn("Event type does not exist for : " + this.identifier);
        }

        return eventType;
    }

    @Override
    public void onEvent(@NotNull User user) {
        // Check if the event is enabled.
        if (!this.section.getBoolean("enabled", true)) return;

        // Check if there is a permission
        // and if the user doesn't have it.
        if (this.section.getKeys().contains("permission")
                && !user.hasPermission(this.section.getString("permission"))) {

            return;
        }

        // Check for commands.
        for (String command : this.section.getListString("commands", new ArrayList<>())) {
            user.executeCommand(command
                    .replace("%message%", this.getEventType().getMessage() == null ? "null" : this.getEventType().getMessage())
            );
        }

        // Check for servers.
        if (!this.section.getListString("servers", new ArrayList<>()).isEmpty()) {
            user.send(this.section.getListString("servers"));
        }

        // Check if there is a discord webhook.
        if (this.section.getKeys().contains(ConfigurationKey.DISCORD_WEBHOOK.getKey())) {
            DiscordWebhookAdapter adapter = new DiscordWebhookAdapter(
                    section.getSection(ConfigurationKey.DISCORD_WEBHOOK.getKey())
            );

            adapter.setPlaceholderParser(string ->
                    PlaceholderManager.parse(string, null, user)
                            .replace("%message%", this.getEventType().getMessage() == null ? "null" : this.getEventType().getMessage())
            );

            adapter.send();
        }
    }
}

