package com.github.smuddgge.leaf.dependencys;

/**
 * Represents the protocolize dependency.
 * Contains utility methods.
 */
public class ProtocolizeDependency {

    /**
     * Used to check if the protocolize dependency is enabled.
     * 
     * @return True if enabled.
     */
    public static boolean isEnabled() {
        return ProtocolizeDependency.isInventoryEnabled();
    }

    /**
     * Used to check if the protocolize dependency is enabled and inventories
     * are available.
     * 
     * @return True if enabled.
     */
    public static boolean isInventoryEnabled() {
        try {
            Class.forName("dev/simplix/protocolize/api/inventory/Inventory");
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Used to get the dependency message.
     * 
     * @return The requested string.
     */
    public static String getDependencyMessage() {
        return "Protocolize : https://www.spigotmc.org/resources/protocolize-protocollib-for-bungeecord-waterfall-velocity.63778/";
    }
}