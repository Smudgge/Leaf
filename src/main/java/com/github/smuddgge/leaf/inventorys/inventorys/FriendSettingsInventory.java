package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.database.records.FriendSettingsRecord;
import com.github.smuddgge.leaf.database.tables.FriendSettingsTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.CustomInventory;
import com.github.smuddgge.leaf.inventorys.InventoryItem;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.record.RecordField;
import dev.simplix.protocolize.api.item.ItemStack;

public class FriendSettingsInventory extends CustomInventory {

    private final FriendSettingsRecord friendSettingsRecord;

    /**
     * Used to create a custom inventory.
     *
     * @param section The parent configuration section to the inventory.
     * @param user    The user that will open the inventory.
     */
    public FriendSettingsInventory(ConfigurationSection section, User user) {
        super(section, user, "inventory");

        FriendSettingsTable friendSettingsTable = Leaf.getDatabase().getTable(FriendSettingsTable.class);
        FriendSettingsRecord friendSettings = friendSettingsTable.getFirstRecord(new Query().match("playerUuid", user.getUniqueId()));

        if (friendSettings == null) {
            this.friendSettingsRecord = new FriendSettingsRecord();
            this.friendSettingsRecord.playerUuid = user.getUniqueId().toString();
            return;
        }

        this.friendSettingsRecord = friendSettings;
    }

    @Override
    public ItemStack onLoadItemWithFunction(InventoryItem inventoryItem) {
        String functionType = inventoryItem.getFunctionSection().getString("type", null);
        if (functionType == null) return inventoryItem.getItemStack();

        RecordField field = this.friendSettingsRecord.getField(functionType);
        assert field != null;
        assert field.getValue() != null;

        inventoryItem.addPlaceholder("%" + functionType + "%", (String) field.getValue());

        for (int slot : inventoryItem.getSlots(this.getInventoryType())) {
            this.addAction(slot, () -> {
                RecordField recordField = this.friendSettingsRecord.getField(functionType);
                assert recordField != null;

                if (recordField.getValue() == "true") {
                    recordField.setValue("false");
                } else {
                    recordField.setValue("true");
                }

                FriendSettingsTable friendSettingsTable = Leaf.getDatabase().getTable(FriendSettingsTable.class);
                friendSettingsTable.insertRecord(this.friendSettingsRecord);
                this.load();
            });
        }

        if (inventoryItem.getFunctionSection().getKeys().contains("true") && field.getValue().equals("true")) {
            return inventoryItem.append(inventoryItem.getFunctionSection().getSection("true")).getItemStack();
        }

        return inventoryItem.getItemStack();
    }
}
