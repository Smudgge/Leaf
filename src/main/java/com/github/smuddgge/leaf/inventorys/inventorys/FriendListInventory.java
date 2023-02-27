package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.records.FriendMailRecord;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendMailTable;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.CustomInventory;
import com.github.smuddgge.leaf.inventorys.InventoryItem;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.utility.DateAndTime;
import com.github.smuddgge.squishydatabase.Query;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.item.ItemStack;
import net.kyori.adventure.text.Component;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the friend list inventory.
 */
public class FriendListInventory extends CustomInventory {

    protected List<FriendRecord> friendRecords;

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
        this.friendRecords = Leaf.getDatabase().getTable(FriendTable.class).getRecordList(
                new Query().match("playerUuid", user.getUniqueId().toString())
        );
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
        this.friendRecords = Leaf.getDatabase().getTable(FriendTable.class).getRecordList(
                new Query().match("playerUuid", listContextUserUuid)
        );
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
                this.inventory.item(slot, this.parseCustomPlaceholders(item, this.friendRecords.get(recordIndex)));
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
        // Get tables.
        FriendMailTable friendMailTable = Leaf.getDatabase().getTable(FriendMailTable.class);
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);

        // Get records.
        FriendMailRecord friendMailRecord = friendMailTable.getLatest(record.playerUuid, record.friendPlayerUuid);
        PlayerRecord friendPlayerRecord = playerTable.getFirstRecord(new Query().match("uuid", record.friendPlayerUuid));
        assert friendPlayerRecord != null;

        // Get name and optional player.
        String friendsName = friendPlayerRecord.name;
        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(friendPlayerRecord.uuid);

        // Create fake mail record if none exist.
        if (friendMailRecord == null) {
            friendMailRecord = new FriendMailRecord();
            friendMailRecord.message = "None";
            friendMailRecord.viewedBoolean = "true";
        }

        // Parse display name.
        String tempName = item.displayName(true);
        if (optionalPlayer.isPresent()) {
            tempName = PlaceholderManager.parse(tempName, null, new User(optionalPlayer.get()));
        }
        item.displayName(MessageManager.convert(tempName
                .replace("%name%", friendsName)
                .replace("%date%", DateAndTime.convert(record.dateCreated))
                .replace("%last_mail%", friendMailRecord.message)
                .replace("%mail_status%", friendMailRecord.getStatus())));

        // Parse lore.
        List<Component> lore = new ArrayList<>();
        for (Object line : item.lore(true)) {
            String tempLine = (String) line;
            if (optionalPlayer.isPresent()) {
                tempLine = PlaceholderManager.parse(tempLine, null, new User(optionalPlayer.get()));
            }
            lore.add(MessageManager.convert(tempLine
                    .replace("%name%", friendsName)
                    .replace("%date%", DateAndTime.convert(record.dateCreated))
                    .replace("%last_mail%", friendMailRecord.message)
                    .replace("%mail_status%", friendMailRecord.getStatus())
            ));
        }
        item.lore(lore, false);

        // Parse nbt.
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
