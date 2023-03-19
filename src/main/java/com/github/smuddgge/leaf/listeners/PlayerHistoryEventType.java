package com.github.smuddgge.leaf.listeners;

/**
 * Represents the type of history to append to the record.
 */
public enum PlayerHistoryEventType {
    JOIN("&a+"), LEAVE("&c-");

    private final String prefix;

    /**
     * Used to create an event type.
     *
     * @param prefix The history's prefix.
     */
    PlayerHistoryEventType(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Used to get the history's prefix
     *
     * @return The history's prefix.
     */
    public String getPrefix() {
        return this.prefix;
    }
}
