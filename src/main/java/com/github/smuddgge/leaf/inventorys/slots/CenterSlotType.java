package com.github.smuddgge.leaf.inventorys.slots;

import com.github.smuddgge.leaf.MessageManager;
import dev.simplix.protocolize.data.inventory.InventoryType;

public class CenterSlotType implements SlotType {

    @Override
    public boolean match(String slot) {
        return slot.matches("^center");
    }

    @Override
    public int[] parse(String slot, InventoryType inventoryType) {
        String argument = slot.replace("center", "");

        try {
            int col = Integer.parseInt(argument);
            return new int[]{4 + (col * 9) - 1};

        } catch (NumberFormatException exception) {
            MessageManager.warn("Invalid center slot type : &f" + slot);
            return new int[]{};
        }
    }
}
