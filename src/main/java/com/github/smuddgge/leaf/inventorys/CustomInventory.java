package com.github.smuddgge.leaf.inventorys;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import dev.simplix.protocolize.data.inventory.InventoryType;

public class CustomInventory extends InventoryInterface {

    /**
     * The parent configuration section to the inventory.
     */
    private ConfigurationSection section;

    @Override
    public InventoryType getInventoryType() {
        try {
            return InventoryType.valueOf(this.getInventorySection().getString("size", "GENERIC_9X6"));
        } catch (IllegalArgumentException exception) {
            MessageManager.log("&eInventory size invalid! Defaulting to &fGENERIC_9X6");
            return InventoryType.GENERIC_9X6;
        }
    }

    @Override
    public String getTitle() {
        return this.getInventorySection().getString("title", "&8&lInventory");
    }

    @Override
    protected void load(User user) {
        if (this.getInventorySection().getSection("content") == null) return;

        for (String slot : this.getInventorySection().getSection("content").getKeys()) {
            InventoryItem inventoryItem = new InventoryItem(this.getInventorySection().getSection("content").getSection(slot), slot);

            for (int slots : inventoryItem.getSlots(this.getInventoryType())) {

            }
        }
    }

    /**
     * Used to get the inventory section.
     *
     * @return The requested inventory configuration section.
     */
    private ConfigurationSection getInventorySection() {
        return this.section.getSection("inventory");
    }
}
