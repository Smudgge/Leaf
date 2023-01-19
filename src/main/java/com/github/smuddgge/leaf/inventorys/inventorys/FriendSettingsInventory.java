package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.Field;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendSettingsRecord;
import com.github.smuddgge.leaf.database.tables.FriendSettingsTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.CustomInventory;
import com.github.smuddgge.leaf.inventorys.InventoryItem;
import dev.simplix.protocolize.api.item.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

public class FriendSettingsInventory extends CustomInventory {

    private final FriendSettingsRecord friendSettingsRecord;
    private String toggleProxyJoin;

    /**
     * Used to create a custom inventory.
     *
     * @param section The parent configuration section to the inventory.
     * @param user    The user that will open the inventory.
     */
    public FriendSettingsInventory(ConfigurationSection section, User user) {
        super(section, user);

        FriendSettingsTable friendSettingsTable = (FriendSettingsTable) Leaf.getDatabase().getTable("FriendSettings");
        ArrayList<Record> result = friendSettingsTable.getRecord("playerUuid", user.getUniqueId());

        if (result.size() == 0) {
            this.friendSettingsRecord = new FriendSettingsRecord();
            this.friendSettingsRecord.playerUuid = user.getUniqueId().toString();
            return;
        }
        this.friendSettingsRecord = (FriendSettingsRecord) result.get(0);
    }

    @Override
    public ItemStack onLoadItemWithFunction(InventoryItem inventoryItem) {
        String functionType = inventoryItem.getFunctionSection().getString("type", null);
        if (functionType == null) return inventoryItem.getItemStack();

        Field field = this.friendSettingsRecord.getField(functionType);

        inventoryItem.addPlaceholder("%" + functionType + "%", (String) field.getValue());

        for (int slot : inventoryItem.getSlots(this.getInventoryType())) {
            this.addAction(slot, () -> {
                this.friendSettingsRecord.toggleBoolean(functionType);
                this.load();
            });
        }

        if (inventoryItem.getFunctionSection().getKeys().contains("true") && field.getValue().equals("true")) {
            return inventoryItem.append(inventoryItem.getFunctionSection().getSection("true")).getItemStack();
        }

        return inventoryItem.getItemStack();
    }
}
