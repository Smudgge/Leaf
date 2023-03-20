package com.github.smuddgge.leaf.events;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents types of events.
 */
public enum EventType {
    PLAYER_JOIN("playerjoin"), PLAYER_SWITCH("playerswitch");

    private final String typeIdentifier;

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
     * Used to get the type identifier.
     *
     * @return The enums type identifier.
     */
    public String getTypeIdentifier() {
        return this.typeIdentifier;
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
