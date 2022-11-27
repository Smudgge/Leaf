package com.github.smuddgge.leaf.placeholders;

import com.github.smuddgge.leaf.datatype.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the placeholder manager.
 */
public class PlaceholderManager {

    private static final List<Placeholder> placeholderList = new ArrayList<>();

    /**
     * User to parse placeholders in a message with context of a user.
     * <ul>
     *     <li>If the user is null it will replace as if there is no context.</li>
     *     <li>If the placeholder type is null, it will not filter the placeholders.</li>
     * </ul>
     *
     * @param message    The message to parse.
     * @param filterType The type of placeholder to filter.
     *                   It will only replace this type of placeholder.
     * @param user       The user.
     * @return The converted string.
     */
    public static String parse(String message, PlaceholderType filterType, User user) {
        for (Placeholder placeholder : PlaceholderManager.placeholderList) {

            // If we are filtering and the placeholder is not the filter type, skip.
            if (filterType != null && placeholder.getType() != filterType) continue;

            PlaceholderType placeholderType = placeholder.getType();
            String replacer = placeholderType.getPrefix() + placeholder.getIdentifier() + placeholderType.getSuffix();

            message = message.replace(replacer, placeholder.getValue(user));
        }

        return message;
    }

    /**
     * Used to parse placeholders in a message.
     * <ul>
     *     <li>If the placeholder type is null, it will not filter the placeholders.</li>
     * </ul>
     *
     * @param message    The message to parse.
     * @param filterType The type of placeholder to filter.
     *                   It will only replace this type of placeholder.
     * @return The converted string.
     */
    public static String parse(String message, PlaceholderType filterType) {
        return PlaceholderManager.parse(message, filterType, null);
    }

    /**
     * Used to register a placeholder in the manager.
     *
     * @param placeholder The placeholder to register.
     */
    public static void register(Placeholder placeholder) {
        PlaceholderManager.placeholderList.add(placeholder);
    }

    /**
     * Used to unregister a placeholder in the manager.
     *
     * @param identifier The placeholder's identifier.
     */
    public static void unregister(String identifier) {
        for (Placeholder placeholder : PlaceholderManager.placeholderList) {
            if (!Objects.equals(placeholder.getIdentifier(), identifier)) continue;

            PlaceholderManager.placeholderList.remove(placeholder);
        }
    }
}
