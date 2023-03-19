package com.github.smuddgge.leaf.events;

import com.github.smuddgge.leaf.datatype.User;
import org.jetbrains.annotations.NotNull;

public interface Event {

    /**
     * Used to get the event identifier.
     *
     * @return The events identifier.
     */
    String getIdentifier();

    /**
     * Used to get the event type that will trigger the event.
     *
     * @return The event type.
     */
    EventType getEventType();

    /**
     * This is fired when the event is triggered.
     *
     * @param user The instance of a user.
     */
    void onEvent(@NotNull User user);
}
