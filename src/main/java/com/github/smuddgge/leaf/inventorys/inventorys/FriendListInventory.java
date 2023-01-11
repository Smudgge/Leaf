package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendMailRecord;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.tables.FriendMailTable;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.CustomInventory;
import com.github.smuddgge.leaf.inventorys.InventoryItem;
import dev.simplix.protocolize.api.item.ItemStack;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the friend list inventory.
 */
public class FriendListInventory extends CustomInventory {

    private ArrayList<Record> friendRecords;

    /**
     * Used to create a {@link FriendListInventory} open on the first page.
     * The first page is page 0.
     *
     * @param section The parent configuration section to the inventory.
     * @param user The user that is opening the inventory.
     */
    public FriendListInventory(ConfigurationSection section, User user) {
        super(section, user);

        // Check if the database is disabled.
        if (Leaf.getDatabase().isDisabled()) return;

        // Load all the friend records.
        FriendTable friendTable = (FriendTable) Leaf.getDatabase().getTable("Friend");
        this.friendRecords = friendTable.getRecord("playerUuid", this.user.getUniqueId());
    }

    @Override
    public ItemStack onLoadItemWithFunction(InventoryItem inventoryItem) {
        if (Leaf.getDatabase().isDisabled()) return inventoryItem.getItemStack();

        return switch (inventoryItem.getFunctionSection().getString("type")) {
            default -> this.onLoadPlayer(inventoryItem);
            case "last_page" -> this.onLastPage(inventoryItem);
            case "next_page" -> this.onNextPage(inventoryItem, this.friendRecords.size());
        };
    }

    /**
     * Called when a player item is loaded.
     *
     * @param inventoryItem The instance of the inventory item.
     * @return The updated item stack.
     */
    private ItemStack onLoadPlayer(InventoryItem inventoryItem) {
        ItemStack item = this.appendPlayerItemStack(inventoryItem);
        Map<Integer, String> mockInventory = this.getInventoryOf("player");

        int friendsPerPage = this.getInventoryOf("player").size();
        int recordIndex = (friendsPerPage * page) - friendsPerPage;

        for (Integer slot : mockInventory.keySet()) {

            // Check if the record exists.
            if (this.friendRecords.size() - 1 < recordIndex) return null;

            // Add the item to the inventory.
            this.inventory.item(slot, this.parseCustomPlaceholders(item, (FriendRecord) this.friendRecords.get(recordIndex)));

            // Increase record index.
            recordIndex ++;
        }

        return null;
    }

    /**
     * Used to get the player item stack.
     *
     * @param inventoryItem The current inventory item.
     * @return The requested item stack.
     */
    private ItemStack appendPlayerItemStack(InventoryItem inventoryItem) {
        if (!this.section.getKeys().contains("player")) return inventoryItem.getItemStack();
        return inventoryItem.append(this.section.getSection("player")).getItemStack();
    }

    /**
     * Used to parse placeholders on an item for a friend record.
     *
     * @param item The item to parse.
     * @param record The record to parse in context of.
     * @return The requested item stack.
     */
    private ItemStack parseCustomPlaceholders(ItemStack item, FriendRecord record) {
        FriendMailTable friendMailTable = (FriendMailTable) Leaf.getDatabase().getTable("FriendMail");
        FriendMailRecord friendMailRecord = friendMailTable.getLatest(record.playerUuid, record.friendPlayerUuid);

        item.displayName(MessageManager.convertToLegacy(item.displayName(true))
                .replace("%name%", record.friendNameFormatted)
                .replace("%date%", record.dateCreated)
                .replace("%last_mail%", friendMailRecord.message)
                .replace("%mail_status%", friendMailRecord.getStatus()));

        List<String> lore = new ArrayList<>();
        for (Object line : item.lore()) {
            lore.add(MessageManager.convertToLegacy((String) line)
                    .replace("%name%", record.friendNameFormatted)
                    .replace("%date%", record.dateCreated)
                    .replace("%last_mail%", friendMailRecord.message)
                    .replace("%mail_status%", friendMailRecord.getStatus()));
        }
        item.lore(lore, true);

        CompoundTag compoundTag = item.nbtData();
        CompoundTag toAdd = new CompoundTag();
        for (Map.Entry<String, Tag<?>> tag : compoundTag.entrySet()) {
            toAdd.putString(tag.getKey(), tag.getValue().toString()
                    .replace("%name%", record.friendNameFormatted)
                    .replace("%date%", record.dateCreated)
                    .replace("%last_mail%", friendMailRecord.message)
                    .replace("%mail_status%", friendMailRecord.getStatus()));
        }
        item.nbtData(toAdd);

        return item;
    }
}
