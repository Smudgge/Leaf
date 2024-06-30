package com.github.smuddgge.leaf.placeholders;

import com.github.smuddgge.leaf.MessageManager;
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

            // Check if the message contains the placeholder.
            if (!message.contains(placeholder.getString())) continue;

            String replacer = placeholder.getString();
            String value = placeholder.getValue(user);
            if (value == null) value = "null";

            // Check if it is a custom placeholder.
            if (placeholder.getType() == PlaceholderType.CUSTOM) {
                value = PlaceholderManager.parse(value, PlaceholderType.STANDARD, user);
            }

            message = message.replace(replacer, value);
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
     * Used to parse placeholders in a message.
     *
     * @param message The message to parse.
     * @return The converted string.
     */
    public static String parse(String message) {
        return PlaceholderManager.parse(message, null, null);
    }

    /**
     * Used to get a placeholder.
     *
     * @param identifier The placeholder's identifier.
     * @return The instance of the placeholder.
     */
    public static Placeholder get(String identifier) {
        for (Placeholder placeholder : PlaceholderManager.placeholderList) {
            if (!Objects.equals(placeholder.getIdentifier(), identifier)) continue;

            return placeholder;
        }
        return null;
    }

    /**
     * Used to register a placeholder in the manager.
     *
     * @param placeholder The placeholder to register.
     */
    public static void register(Placeholder placeholder) {

        // Check if the placeholder identifier has already been registered.
        if (PlaceholderManager.get(placeholder.getIdentifier()) != null) {
            MessageManager.warn("&7[Placeholders] &eDuplicate placeholder : " + placeholder.getIdentifier() + " (This version of the placeholder has been disabled)");
            return;
        }

        PlaceholderManager.placeholderList.add(placeholder);
        MessageManager.log("&7[Placeholders] &aRegistered &7placeholder : " + placeholder.getString());
    }

    /**
     * Used to unregister a placeholder in the manager.
     *
     * @param identifier The placeholder's identifier.
     */
    public static void unregister(String identifier) {
        Placeholder placeholder = PlaceholderManager.get(identifier);

        if (placeholder == null) return;

        MessageManager.log("&7[Placeholders] &cUnregistered &7placeholder : " + identifier);

        PlaceholderManager.placeholderList.remove(placeholder);
    }
}
