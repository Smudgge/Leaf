package com.github.smuddgge.leaf.dependencys;

import com.github.smuddgge.leaf.Leaf;

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
        return Leaf.getServer().getPluginManager().getPlugin("protocolize").isPresent();
    }

    /**
     * Used to check if the protocolize dependency is enabled and inventories
     * are available.
     *
     * @return True if enabled.
     */
    public static boolean isInventoryEnabled() {
        try {

            Class.forName("dev.simplix.protocolize.api.inventory.Inventory");
            new ProtocolizeHelper().check();
            return isEnabled();

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