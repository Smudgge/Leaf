package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendMailRecord;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendMailTable;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.CustomInventory;
import com.github.smuddgge.leaf.inventorys.InventoryItem;
import com.github.smuddgge.leaf.utility.DateAndTime;
import dev.simplix.protocolize.api.item.ItemStack;
import net.kyori.adventure.text.Component;
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
     * @param user    The user that is opening the inventory.
     */
    public FriendListInventory(ConfigurationSection section, User user) {
        super(section, user, "inventory");

        // Check if the database is disabled.
        if (Leaf.isDatabaseDisabled()) return;

        // Load all the friend records.
        FriendTable friendTable = (FriendTable) Leaf.getDatabase().getTable("Friend");
        this.friendRecords = friendTable.getRecord("playerUuid", user.getUniqueId());
    }

    /**
     * Used to create a {@link FriendListInventory} open on the first page.
     * The first page is 0.
     * The friend list will be in context of the list context user.
     *
     * @param section             The parent configuration section to the inventory.
     * @param user                The user that is opening the inventory.
     * @param listContextUserUuid The user whose friend list should be shown.
     */
    public FriendListInventory(ConfigurationSection section, User user, String listContextUserUuid) {
        super(section, user, "inventory");

        // Check if the database is disabled.
        if (Leaf.isDatabaseDisabled()) return;

        // Load all the friend records in terms of the list context.
        FriendTable friendTable = (FriendTable) Leaf.getDatabase().getTable("Friend");
        this.friendRecords = friendTable.getRecord("playerUuid", listContextUserUuid);
    }

    @Override
    public ItemStack onLoadItemWithFunction(InventoryItem inventoryItem) {
        if (Leaf.isDatabaseDisabled()) return inventoryItem.getItemStack();

        return switch (inventoryItem.getFunctionSection().getString("type")) {
            case "last_page" -> this.onLastPage(inventoryItem);
            case "next_page" -> this.onNextPage(inventoryItem, this.friendRecords.size());
            default -> this.onLoadPlayer(inventoryItem);
        };
    }

    /**
     * Called when a player item is loaded.
     *
     * @param inventoryItem The instance of the inventory item.
     * @return The updated item stack.
     */
    private ItemStack onLoadPlayer(InventoryItem inventoryItem) {
        Map<Integer, String> mockInventory = this.getInventoryOf("player");

        int friendsPerPage = this.getInventoryOf("player").size();
        int recordIndex = (friendsPerPage * this.page) - friendsPerPage;

        for (Integer slot : mockInventory.keySet()) {

            // Check if the record exists.
            if (this.friendRecords.size() - 1 < recordIndex) {
                ItemStack item = this.appendNoPlayerItemStack(inventoryItem);
                this.inventory.item(slot, item);
            } else {
                ItemStack item = this.appendPlayerItemStack(inventoryItem);
                this.inventory.item(slot, this.parseCustomPlaceholders(item, (FriendRecord) this.friendRecords.get(recordIndex)));
            }

            // Increase record index.
            recordIndex++;
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
     * Used to get the no player item stack.
     *
     * @param inventoryItem The current inventory item.
     * @return The requested item stack.
     */
    private ItemStack appendNoPlayerItemStack(InventoryItem inventoryItem) {
        if (!this.section.getKeys().contains("no_player")) return inventoryItem.getItemStack();
        return inventoryItem.append(this.section.getSection("no_player")).getItemStack();
    }

    /**
     * Used to parse placeholders on an item for a friend record.
     *
     * @param item   The item to parse.
     * @param record The record to parse in context of.
     * @return The requested item stack.
     */
    private ItemStack parseCustomPlaceholders(ItemStack item, FriendRecord record) {
        FriendMailTable friendMailTable = (FriendMailTable) Leaf.getDatabase().getTable("FriendMail");
        FriendMailRecord friendMailRecord = friendMailTable.getLatest(record.playerUuid, record.friendPlayerUuid);

        PlayerTable playerTable = (PlayerTable) Leaf.getDatabase().getTable("Player");
        PlayerRecord friendPlayerRecord = playerTable.getPlayer(record.friendPlayerUuid);
        String friendsName = friendPlayerRecord.name;

        if (friendMailRecord == null) {
            friendMailRecord = new FriendMailRecord();
            friendMailRecord.message = "None";
            friendMailRecord.viewedBoolean = "true";
        }

        String tempName = item.displayName(true);
        item.displayName(MessageManager.convert(tempName
                .replace("%name%", friendsName)
                .replace("%date%", DateAndTime.convert(record.dateCreated))
                .replace("%last_mail%", friendMailRecord.message)
                .replace("%mail_status%", friendMailRecord.getStatus())));

        List<Component> lore = new ArrayList<>();
        for (Object line : item.lore(true)) {
            String tempLine = (String) line;
            lore.add(MessageManager.convert(tempLine
                    .replace("%name%", friendsName)
                    .replace("%date%", DateAndTime.convert(record.dateCreated))
                    .replace("%last_mail%", friendMailRecord.message)
                    .replace("%mail_status%", friendMailRecord.getStatus())
            ));
        }
        item.lore(lore, false);

        CompoundTag compoundTag = item.nbtData();
        CompoundTag toAdd = new CompoundTag();
        for (Map.Entry<String, Tag<?>> tag : compoundTag.entrySet()) {
            toAdd.putString(tag.getKey(), tag.getValue().valueToString()
                    .replace("%name%", friendsName)
                    .replace("%date%", DateAndTime.convert(record.dateCreated))
                    .replace("%last_mail%", friendMailRecord.message)
                    .replace("%mail_status%", friendMailRecord.getStatus())
                    .replace("\"", "")
                    .replace("\\", ""));
        }
        item.nbtData(toAdd);

        return item;
    }
}
