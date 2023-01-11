package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.FriendRequestManager;
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
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.item.ItemStack;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        super(section, user);

        // Check if the database is disabled.
        if (Leaf.getDatabase().isDisabled()) return;

        // Load all the friend records.
        FriendRequestTable friendRequestTable = (FriendRequestTable) Leaf.getDatabase().getTable("FriendRequest");
        this.requestRecords = friendRequestTable.getRecord("playerToUuid", this.user.getUniqueId());
    }

    @Override
    public ItemStack onLoadItemWithFunction(InventoryItem inventoryItem) {
        if (Leaf.getDatabase().isDisabled()) return inventoryItem.getItemStack();

        return switch (inventoryItem.getFunctionSection().getString("type")) {
            default -> this.onLoadPlayer(inventoryItem);
            case "last_page" -> this.onLastPage(inventoryItem);
            case "next_page" -> this.onNextPage(inventoryItem, this.requestRecords.size());
        };
    }

    private ItemStack onLoadPlayer(InventoryItem inventoryItem) {
        ItemStack item = this.appendPlayerItemStack(inventoryItem);
        Map<Integer, String> mockInventory = this.getInventoryOf("player");

        int requestsPerPage = this.getInventoryOf("player").size();
        int recordIndex = (requestsPerPage * page) - requestsPerPage;

        for (Integer slot : mockInventory.keySet()) {

            // Check if the record exists.
            if (this.requestRecords.size() - 1 < recordIndex) return null;

            FriendRequestRecord requestRecord = (FriendRequestRecord) this.requestRecords.get(recordIndex);
            PlayerTable playerTable = (PlayerTable) Leaf.getDatabase().getTable("Player");
            String acceptedPlayerName = playerTable.getPlayer(requestRecord.playerFromUuid).name;

            // Add the item to the inventory.
            this.inventory.item(slot, this.parseCustomPlaceholders(item, requestRecord));

            this.addAction(slot, () -> {
                FriendRequestManager.acceptRequest(requestRecord);

                user.sendMessage(PlaceholderManager.parse(
                        this.section.getString("from", "{message} You are now friends with &f<name>"),
                        null, new User(null, acceptedPlayerName)
                ));

                Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(acceptedPlayerName);
                if (optionalPlayer.isEmpty()) return;

                User userSentTo = new User(optionalPlayer.get());

                userSentTo.sendMessage(PlaceholderManager.parse(
                        this.section.getString("sent", "{message} You are now friends with &f<name>"),
                        null, user
                ));

                this.requestRecords.remove(requestRecord);
                this.load();
            });

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
     * Used to parse placeholders on an item for a request record.
     *
     * @param item   The item to parse.
     * @param record The record to parse in context of.
     * @return The requested item stack.
     */
    private ItemStack parseCustomPlaceholders(ItemStack item, FriendRequestRecord record) {
        PlayerTable playerTable = (PlayerTable) Leaf.getDatabase().getTable("Player");
        String name = playerTable.getPlayer(record.playerFromUuid).name;

        item.displayName(MessageManager.convertToLegacy(item.displayName(true))
                .replace("%name%", name));

        List<String> lore = new ArrayList<>();
        for (Object line : item.lore()) {
            lore.add(MessageManager.convertToLegacy((String) line)
                    .replace("%name%", name));
        }
        item.lore(lore, true);

        CompoundTag compoundTag = item.nbtData();
        CompoundTag toAdd = new CompoundTag();
        for (Map.Entry<String, Tag<?>> tag : compoundTag.entrySet()) {
            toAdd.putString(tag.getKey(), tag.getValue().toString()
                    .replace("%name%", name));
        }
        item.nbtData(toAdd);

        return item;
    }
}
