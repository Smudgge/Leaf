package com.github.smuddgge.leaf.events;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.datatype.User;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to manager custom events.
 */
public class EventManager {

    private static final List<Event> eventTypeList = new ArrayList<>();

    /**
     * Used to register an event with the manager.
     *
     * @param event The event to register.
     */
    public static void register(@NotNull Event event) {
        EventManager.eventTypeList.add(event);
        MessageManager.log("&7[Events] &aRegistered &7event : " + event.getIdentifier());
    }

    /**
     * Used to unregister an event with the manager.
     *
     * @param event The event to unregister.
     */
    public static void unRegister(@NotNull Event event) {
        EventManager.eventTypeList.remove(event);
        MessageManager.log("&7[Events] &eUnregistered &7event : " + event.getIdentifier());
    }

    /**
     * Used to run all event with a certain type.
     *
     * @param eventType The instance of an event type.
     * @param user      The instance of a user.
     */
    public static void runEvent(@NotNull EventType eventType, @NotNull User user) {
        for (Event event : EventManager.eventTypeList) {
            if (event.getEventType() == null) continue;
            if (event.getEventType() == eventType) event.onEvent(user);
        }
    }
}
