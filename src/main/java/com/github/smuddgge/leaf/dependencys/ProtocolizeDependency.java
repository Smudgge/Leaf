package com.github.smuddgge.leaf.dependencys;

import java.lang;

/**
 * Represents the protocolise dependecy utilitys.
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
     * Used to check if the protocolize dependency is enabled and inventorys
     * are avaliable.
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
     * @return The requirested string.
     */
    public static String getDependencyMessage() {
        return "Protocolize : https://www.spigotmc.org/resources/protocolize-protocollib-for-bungeecord-waterfall-velocity.63778/"
    }
}