package com.github.smuddgge.leaf.inventorys;

import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import dev.simplix.protocolize.data.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a custom inventory item.
 */
public class InventoryItem {

    private final ConfigurationSection section;
    private final String slot;

    /**
     * Used to create a inventory item.
     *
     * @param section The configuration section.
     * @param slot    The slot information.
     */
    public InventoryItem(ConfigurationSection section, String slot) {
        this.section = section;
        this.slot = slot;
    }


    /**
     * Used to get the list of slots.
     *
     * @param inventoryType The type of inventory the slots are in.
     * @return The requested slots
     */
    public List<Integer> getSlots(InventoryType inventoryType) {
        List<Integer> slots = new ArrayList<>();

        for (String argument : this.slot.split(",")) {
            // Check if it's an integer
            if (argument.trim().matches("^[0-9]$")) {
                slots.add(Integer.parseInt(argument.trim()));
            }

            List<Integer> argumentsSlots = SlotManager.parseSlot(argument.trim(), inventoryType);
            for (Integer integer : argumentsSlots) {
                if (slots.contains(integer)) continue;
                slots.add(integer);
            }
        }

        return slots;
    }
}
