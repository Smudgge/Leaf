package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Represents the message manager.
 */
public class MessageManager {

    /**
     * Used to convert a message to a component with colour.
     *
     * @param message The message to convert.
     * @return The requested component.
     */
    public static Component convert(String message) {
        return Component.text()
                .append(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        PlaceholderManager.parse(message)
                ))
                .build();
    }

    /**
     * Used to convert a message to a component with colour.
     *
     * @param message The message to convert.
     * @param user The user context to convert placeholders with.
     * @return The requested component.
     */
    public static Component convert(String message, User user) {
        return Component.text()
                .append(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        PlaceholderManager.parse(message, null, user)
                ))
                .build();
    }

    /**
     * Used to log information into the console with converted colours.
     *
     * @param message The message to send.
     */
    public static void log(String message) {
        message = "&a[Leaf] &7" + message;

        for (String string : message.split("\n")) {
            Leaf.getServer().getConsoleCommandSource().sendMessage(MessageManager.convert(string));
        }
    }

    /**
     * Used to log a warning in the console with converted colours.
     *
     * @param message The message to send.
     */
    public static void warn(String message) {
        message = "&a[Leaf] &e[WARNING] &6" + message;
        for (String string : message.split("\n")) {
            Leaf.getServer().getConsoleCommandSource().sendMessage(MessageManager.convert(string));
        }
    }

    public static void logHeader() {
        String message = "\n" +
                "&a __         ______     ______     ______\n" +
                "&a/\\ \\       /\\  ___\\   /\\  __ \\   /\\  ___\\\n" +
                "&a\\ \\ \\____  \\ \\  __\\   \\ \\  __ \\  \\ \\  __\\\n" +
                "&a \\ \\_____\\  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_\\\n" +
                "&a  \\/_____/   \\/_____/   \\/_/\\/_/   \\/_/\n" +
                "\n" +
                "    &7By Smudge    Version &e" + Leaf.class.getAnnotation(Plugin.class).version();;

        MessageManager.log(message);
    }
}
