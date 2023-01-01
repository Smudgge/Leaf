package com.github.smuddgge.leaf.inventorys.slots;

import dev.simplix.protocolize.data.inventory.InventoryType;

public class BottomSlotType implements SlotType {

    @Override
    public boolean match(String slot) {
        return slot.matches("^bottom$");
    }

    @Override
    public int[] parse(String slot, InventoryType inventoryType) {
        return switch (inventoryType) {
            case GENERIC_3X3 -> new int[]{7, 8, 9};
            case GENERIC_9X1, PLAYER -> new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
            case GENERIC_9X2 -> new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17};
            case GENERIC_9X3 -> new int[]{18, 19, 20, 21, 22, 23, 24, 25, 26};
            case GENERIC_9X4 -> new int[]{27, 28, 29, 30, 31, 32, 33, 34, 35};
            case GENERIC_9X5 -> new int[]{36, 37 ,38, 39, 40, 41, 42, 43, 44};

            case LOOM -> new int[]{2};

            default -> new int[]{45, 46, 47, 48, 49, 50, 51, 52, 53};
        };
    }
}
