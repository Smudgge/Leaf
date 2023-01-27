package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendRequestRecord;
import com.github.smuddgge.leaf.database.tables.FriendRequestTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.CustomInventory;
import com.github.smuddgge.leaf.inventorys.InventoryItem;
import dev.simplix.protocolize.api.item.ItemStack;
import net.kyori.adventure.text.Component;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the friend requests inventory.
 */
public class FriendRequestInventory extends CustomInventory {

    private ArrayList<Record> requestRecords;

    /**
     * Used to create a custom inventory.
     * Used to create a friend request inventory.
     *
     * @param section The parent configuration section to the inventory.
     * @param user    The user that will open the inventory.
     */
    public FriendRequestInventory(ConfigurationSection section, User user) {
        super(section, user, "inventory");

        // Check if the database is disabled.
        if (Leaf.isDatabaseDisabled()) return;

        // Load all the friend records.
        FriendRequestTable friendRequestTable = (FriendRequestTable) Leaf.getDatabase().getTable("FriendRequest");
        this.requestRecords = friendRequestTable.getRecord("playerToUuid", this.user.getUniqueId());
    }

    @Override
    public ItemStack onLoadItemWithFunction(InventoryItem inventoryItem) {
        if (Leaf.isDatabaseDisabled()) return inventoryItem.getItemStack();

        return switch (inventoryItem.getFunctionSection().getString("type")) {
            case "last_page" -> this.onLastPage(inventoryItem);
            case "next_page" -> this.onNextPage(inventoryItem, this.requestRecords.size());
            default -> this.onLoadPlayer(inventoryItem);
        };
    }

    private ItemStack onLoadPlayer(InventoryItem inventoryItem) {
        Map<Integer, String> mockInventory = this.getInventoryOf("player");

        int requestsPerPage = this.getInventoryOf("player").size();
        int recordIndex = (requestsPerPage * page) - requestsPerPage;

        // Get tables.
        FriendRequestTable friendRequestTable = (FriendRequestTable) Leaf.getDatabase().getTable("FriendRequest");
        PlayerTable playerTable = (PlayerTable) Leaf.getDatabase().getTable("Player");

        for (Integer slot : mockInventory.keySet()) {

            // Check if a record for this slot.
            if (this.requestRecords.size() - 1 < recordIndex) {
                ItemStack item = this.appendNoPlayerItemStack(inventoryItem);
                this.inventory.item(slot, item);
                this.addAction(slot, () -> {});
            } else {
                FriendRequestRecord requestRecord = (FriendRequestRecord) this.requestRecords.get(recordIndex);
                String acceptedPlayerName = playerTable.getPlayer(requestRecord.playerFromUuid).name;

                // Check that the record still exists in the database.
                ArrayList<Record> results = friendRequestTable.getRecord("uuid", requestRecord.uuid);
                if (results.isEmpty()) continue;

                // Add the item to the inventory.
                ItemStack item = this.appendPlayerItemStack(inventoryItem);
                this.inventory.item(slot, this.parseCustomPlaceholders(item, requestRecord));

                this.addAction(slot, () -> {
                    this.close();
                    FriendRequestOptionsInventory friendAcceptInventory = new FriendRequestOptionsInventory(
                            this.section, this.user, requestRecord, acceptedPlayerName
                    );
                    friendAcceptInventory.open();
                });
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
     * Used to parse placeholders on an item for a request record.
     *
     * @param item   The item to parse.
     * @param record The record to parse in context of.
     * @return The requested item stack.
     */
    private ItemStack parseCustomPlaceholders(ItemStack item, FriendRequestRecord record) {
        PlayerTable playerTable = (PlayerTable) Leaf.getDatabase().getTable("Player");
        String name = playerTable.getPlayer(record.playerFromUuid).name;

        String tempName = item.displayName(true);
        item.displayName(MessageManager.convert(tempName
                .replace("%name%", name)));

        List<Component> lore = new ArrayList<>();
        for (Object line : item.lore(true)) {
            String tempLine = (String) line;
            lore.add(MessageManager.convert(tempLine
                    .replace("%name%", name)));
        }
        item.lore(lore, false);

        CompoundTag compoundTag = item.nbtData();
        CompoundTag toAdd = new CompoundTag();
        for (Map.Entry<String, Tag<?>> tag : compoundTag.entrySet()) {
            toAdd.putString(tag.getKey(), tag.getValue().valueToString()
                    .replace("%name%", name)
                    .replace("\"", "")
                    .replace("\\", ""));
        }
        item.nbtData(toAdd);

        return item;
    }
}
