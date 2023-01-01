package com.github.smuddgge.leaf.inventorys;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.inventory.InventoryType;

/**
 * Represents a custom inventory
 */
public class CustomInventory extends InventoryInterface {

    protected final ConfigurationSection section;

    /**
     * Used to create a custom inventory.
     *
     * @param section The parent configuration section to the inventory.
     */
    public CustomInventory(ConfigurationSection section) {
        this.section = section;
    }

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

        for (String slotID : this.getInventorySection().getSection("content").getKeys()) {
            InventoryItem inventoryItem = new InventoryItem(this.getInventorySection().getSection("content").getSection(slotID), slotID, user);
            ItemStack item = inventoryItem.getItemStack();

            for (int slot : inventoryItem.getSlots(this.getInventoryType())) {
                this.inventory.item(slot, item);
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
