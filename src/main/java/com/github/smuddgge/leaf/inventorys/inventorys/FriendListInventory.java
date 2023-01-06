package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.CustomInventory;
import com.github.smuddgge.leaf.inventorys.InventoryItem;
import dev.simplix.protocolize.api.item.ItemStack;

import java.util.ArrayList;

public class FriendListInventory extends CustomInventory {

    private User user;
    private int page;
    private ArrayList<Record> friendRecords;

    /**
     * Used to create a {@link FriendListInventory}
     *
     * @param section The parent configuration section to the inventory.
     * @param user The friend list will be in context of this user.
     */
    public FriendListInventory(ConfigurationSection section, User user, int page) {
        super(section);

        if (Leaf.getDatabase().isDisabled()) return;

        this.user = user;
        this.page = page;

        FriendTable friendTable = (FriendTable) Leaf.getDatabase().getTable("Friend");
        this.friendRecords = friendTable.getRecord("playerUuid", this.user.getUniqueId());
    }

    @Override
    public ItemStack onLoadItemWithFunction(InventoryItem inventoryItem) {
        if (Leaf.getDatabase().isDisabled()) return inventoryItem.getItemStack();

        switch (inventoryItem.getFunctionSection().getString("type")) {
            default:
                return onLoadPlayer(inventoryItem);
        }
    }

    /**
     * Called when a player item is loaded.
     *
     * @param inventoryItem The instance of the inventory item.
     * @return The updated item stack.
     */
    private ItemStack onLoadPlayer(InventoryItem inventoryItem) {

        return null;
    }
}
