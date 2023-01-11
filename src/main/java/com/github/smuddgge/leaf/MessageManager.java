package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.HashMap;
import java.util.UUID;

/**
 * Represents the message manager.
 */
public class MessageManager {

    /**
     * List of players and who they last messaged.
     */
    private static final HashMap<UUID, UUID> lastMessaged = new HashMap<UUID, UUID>();

    /**
     * Used to set who a player last messaged.
     *
     * @param player       Player that sent the message.
     * @param lastMessaged Player the message was sent to.
     */
    public static void setLastMessaged(UUID player, UUID lastMessaged) {
        MessageManager.lastMessaged.put(player, lastMessaged);
        MessageManager.lastMessaged.put(lastMessaged, player);
    }

    /**
     * Used to remove a player from the last messaged list.
     *
     * @param player The player to remove.
     */
    public static void removeLastMessaged(UUID player) {
        MessageManager.lastMessaged.remove(player);
    }

    /**
     * Used to get who is messaging a player.
     *
     * @param player Player to get.
     */
    public static UUID getLastMessaged(UUID player) {
        return MessageManager.lastMessaged.get(player);
    }

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
     * @param user    The user context to convert placeholders with.
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
     * Used to convert standard messages into legacy messages.
     *
     * @param message The message to convert.
     * @return The requested string.
     */
    public static String convertToLegacy(String message) {
        return "§r" + message.replace("&", "§");
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
     * Used to log information into the console.
     *
     * @param component The component.
     */
    public static void log(Component component) {
        Leaf.getServer().getConsoleCommandSource().sendMessage(component);
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
                "    &7By Smudge    Version &e" + Leaf.class.getAnnotation(Plugin.class).version();

        MessageManager.log(message);
    }
}
