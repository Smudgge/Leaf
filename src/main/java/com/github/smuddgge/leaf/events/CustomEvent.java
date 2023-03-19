package com.github.smuddgge.leaf.events;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Represents a custom event.
 */
public record CustomEvent(String identifier,
                          ConfigurationSection section) implements Event {

    /**
     * Used to create a custom event.
     *
     * @param identifier The events identifier.
     * @param section    The configuration section attached to the identifier.
     */
    public CustomEvent {
    }

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
        System.out.println("event fired");
        // Check if the event is enabled.
        if (!this.section.getBoolean("enabled", true)) return;

        // Check for commands.
        for (String command : this.section.getListString("commands", new ArrayList<>())) {
            user.executeCommand(command);
        }

        // Check for servers.
        if (this.section.getListString("servers", new ArrayList<>()).size() > 0) {
            System.out.println("sending");
            user.send(this.section.getListString("servers"));
        }
    }
}

