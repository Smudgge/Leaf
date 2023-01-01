package com.github.smuddgge.leaf.inventorys.slots;

import dev.simplix.protocolize.data.inventory.InventoryType;

/**
 * Represents a slot type
 */
public interface SlotType {

    /**
     * Used to check if a string is this slot type.
     *
     * @param slot The slot to check.
     * @return True if the string matches this slot type.
     */
    boolean match(String slot);

    /**
     * Used to parse the slot as this slot type.
     *
     * @param slot          The slot to parse.
     * @param inventoryType The type of inventory the slot is in.
     * @return The list of slots requested.
     */
    int[] parse(String slot, InventoryType inventoryType);
}
