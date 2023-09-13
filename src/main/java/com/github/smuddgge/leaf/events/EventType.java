package com.github.smuddgge.leaf.events;

import com.github.smuddgge.leaf.events.type.DiscordEvent;
import com.github.smuddgge.leaf.events.type.StandardEvent;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents types of events.
 */
public enum EventType {
    PLAYER_JOIN("playerjoin") {
        @Override
        public @NotNull Event create(@NotNull String identifier, @NotNull ConfigurationSection section) {
            return new StandardEvent(identifier, section, this);
        }
    },
    PLAYER_SWITCH("playerswitch") {
        @Override
        public @NotNull Event create(@NotNull String identifier, @NotNull ConfigurationSection section) {
            return new StandardEvent(identifier, section, this);
        }
    },
    PLAYER_LEAVE("playerleave") {
        @Override
        public @NotNull Event create(@NotNull String identifier, @NotNull ConfigurationSection section) {
            return new StandardEvent(identifier, section, this);
        }
    },
    PLAYER_CHAT("playerchat") {
        @Override
        public @NotNull Event create(@NotNull String identifier, @NotNull ConfigurationSection section) {
            return new StandardEvent(identifier, section, this);
        }
    },
    DISCORD_MESSAGE("discordmessage") {
        @Override
        public @NotNull Event create(@NotNull String identifier, @NotNull ConfigurationSection section) {
            return new DiscordEvent(identifier, section, this);
        }
    };

    private final String typeIdentifier;
    private String message;

    /**
     * Used to create an event type.
     *
     * @param typeIdentifier The type that is stated
     *                       in the configuration.
     */
    EventType(String typeIdentifier) {
        this.typeIdentifier = typeIdentifier;
    }

    /**
     * Used to create a new instance of the event type.
     *
     * @param identifier The instance of the identifier.
     * @param section    The identifier's section.
     * @return The instance of the new event.
     */
    public abstract @NotNull Event create(@NotNull String identifier, @NotNull ConfigurationSection section);

    /**
     * Used to set a message placeholder.
     *
     * @param message The instance of a message.
     * @return This instance.
     */
    public @NotNull EventType setMessage(@Nullable String message) {
        this.message = message;
        return this;
    }

    /**
     * Used to get the type identifier.
     *
     * @return The enums type identifier.
     */
    public String getTypeIdentifier() {
        return this.typeIdentifier;
    }

    /**
     * Used to get the message value.
     *
     * @return The message.
     */
    public @Nullable String getMessage() {
        return this.message;
    }

    /**
     * Used to get an event type given a type identifier.
     *
     * @param typeIdentifier The name of a type identifier.
     * @return The requested event type.
     * If the event type does not exist, it will return null.
     */
    public static @Nullable EventType getFromType(@NotNull String typeIdentifier) {
        for (EventType eventType : EventType.values()) {
            if (Objects.equals(eventType.getTypeIdentifier(), typeIdentifier)) return eventType;
        }
        return null;
    }
}
