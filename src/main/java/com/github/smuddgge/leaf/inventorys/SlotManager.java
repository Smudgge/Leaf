package com.github.smuddgge.leaf.inventorys;

import com.github.smuddgge.leaf.inventorys.slots.*;
import dev.simplix.protocolize.data.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the slot manager.
 */
public class SlotManager {

    private static final List<SlotType> slotTypeList = new ArrayList<>();

    public static void setup() {
        SlotManager.slotTypeList.add(new BottomSlotType());
        SlotManager.slotTypeList.add(new CenterSlotType());
        SlotManager.slotTypeList.add(new RangeSlotType());
        SlotManager.slotTypeList.add(new RowSlotType());
        SlotManager.slotTypeList.add(new TopSlotType());
    }

    /**
     * Used to parse a slot into individual slots.
     *
     * @param slot          The slot to parse.
     * @param inventoryType The inventory type the slot is in.
     * @return The requested slots.
     */
    public static List<Integer> parseSlot(String slot, InventoryType inventoryType) {
        for (SlotType slotType : SlotManager.slotTypeList) {
            if (!slotType.match(slot)) continue;

            return Arrays.stream(slotType.parse(slot, inventoryType)).boxed().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
