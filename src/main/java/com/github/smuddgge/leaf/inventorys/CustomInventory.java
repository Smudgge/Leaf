package com.github.smuddgge.leaf.inventorys;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.inventory.InventoryType;

import java.util.*;

/**
 * Represents a custom inventory
 */
public abstract class CustomInventory extends InventoryInterface {

    protected int page = 1;
    protected final ConfigurationSection section;
    protected final String inventoryID;

    /**
     * Used to create a custom inventory.
     *
     * @param section     The parent configuration section to the inventory.
     * @param user        The user that will open the inventory.
     * @param inventoryID The identifier if the inventory in the configuration section.
     */
    public CustomInventory(ConfigurationSection section, User user, String inventoryID) {
        super(user);

        this.section = section;
        this.inventoryID = inventoryID;

        // Check if the inventory is empty
        if (this.getInventorySection().getKeys().size() == 0) {
            MessageManager.log("&eInventory section found empty in configuration!");
            MessageManager.log(section.getData().toString());
        }
    }

    /**
     * Called when an item is loaded and contains a function before adding to the inventory.
     * A function are used as command type specific items.
     *
     * @param inventoryItem The instance of the inventory item.
     * @return The item stack that will be used in the inventory instead.
     */
    public abstract ItemStack onLoadItemWithFunction(InventoryItem inventoryItem);

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
    protected void load() {
        if (this.getInventorySection().getSection("content") == null) return;

        this.resetActions();

        for (InventoryItem inventoryItem : this.getInventoryItems()) {
            ItemStack item = inventoryItem.getItemStack();

            // Check if the inventory item contains a function.
            if (inventoryItem.isFunction()) {
                item = this.onLoadItemWithFunction(inventoryItem);
            }

            // Check if the item is null.
            if (item == null) continue;

            // Check if the item contains a command.
            List<String> commands = inventoryItem.getSection().getListString("commands");
            this.addCommandList(commands, inventoryItem);

            // For each slot, set the item.
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
    public ConfigurationSection getInventorySection() {
        return this.section.getSection(this.inventoryID);
    }

    /**
     * Used to get the list of inventory items configured in this inventory.
     *
     * @return The requested list of items.
     */
    public List<InventoryItem> getInventoryItems() {
        List<InventoryItem> inventoryItems = new ArrayList<>();

        for (String slotID : this.getInventorySection().getSection("content").getKeys()) {
            InventoryItem inventoryItem = new InventoryItem(
                    this.getInventorySection().getSection("content").getSection(slotID), slotID, this.user
            );
            inventoryItems.add(inventoryItem);
        }

        return inventoryItems;
    }

    /**
     * Used to get a mock inventory of a function type.
     * This is used to stop slot overlapping.
     *
     * @param functionType The function type to check for.
     * @return The requested mock inventory.
     */
    public Map<Integer, String> getInventoryOf(String functionType) {
        Map<Integer, String> mockInventory = new HashMap<>();

        for (InventoryItem inventoryItem : this.getInventoryItems()) {
            // Check if it is the function given.
            if (!inventoryItem.isFunction()) continue;
            if (!(Objects.equals(inventoryItem.getFunctionSection().getString("type", ""), functionType))) continue;

            // Add the slots.
            for (Integer slot : inventoryItem.getSlots(this.getInventoryType())) {
                mockInventory.put(slot, functionType);
            }
        }

        return mockInventory;
    }

    /**
     * Called when a next page item is loaded.
     *
     * @param inventoryItem The instance of the inventory item.
     * @return The requested item stack.
     */
    protected ItemStack onNextPage(InventoryItem inventoryItem, int recordsSize) {
        int perPage = this.getInventoryOf("player").size();
        int recordIndex = (perPage * page) - perPage - 1;

        if (recordsSize - 1 < recordIndex) {
            if (!inventoryItem.getFunctionSection().getBoolean("always_show", false)) {
                for (Integer slot : inventoryItem.getSlots(this.getInventoryType())) {
                    this.addAction(slot, () -> {
                    });
                }
                return null;
            }
        } else {
            for (Integer slot : inventoryItem.getSlots(this.getInventoryType())) {
                this.addAction(slot, () -> {
                    this.page += 1;
                    this.load();
                });
            }
        }

        return inventoryItem.getItemStack();
    }

    /**
     * Called when a last page item is loaded.
     *
     * @param inventoryItem The instance of the inventory item.
     * @return The requested item stack.
     */
    protected ItemStack onLastPage(InventoryItem inventoryItem) {
        if (page <= 1) {
            if (!inventoryItem.getFunctionSection().getBoolean("always_show", false)) {
                for (Integer slot : inventoryItem.getSlots(this.getInventoryType())) {
                    this.addAction(slot, () -> {
                    });
                }
                return null;
            }
        } else {
            for (Integer slot : inventoryItem.getSlots(this.getInventoryType())) {
                this.addAction(slot, () -> {
                    this.page -= 1;
                    this.load();
                });
            }
        }

        return inventoryItem.getItemStack();
    }
}
