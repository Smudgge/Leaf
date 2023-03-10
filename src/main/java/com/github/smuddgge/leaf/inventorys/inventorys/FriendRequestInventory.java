package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.records.FriendRequestRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendRequestTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.CustomInventory;
import com.github.smuddgge.leaf.inventorys.InventoryItem;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishydatabase.Query;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.item.ItemStack;
import net.kyori.adventure.text.Component;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

import java.util.*;

/**
 * Represents the friend requests inventory.
 */
public class FriendRequestInventory extends CustomInventory {

    private List<FriendRequestRecord> requestRecords;

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
        this.requestRecords = Leaf.getDatabase().getTable(FriendRequestTable.class).getRecordList(
                new Query().match("playerToUuid", this.user.getUniqueId())
        );
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
        FriendRequestTable friendRequestTable = Leaf.getDatabase().getTable(FriendRequestTable.class);
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);

        for (Integer slot : mockInventory.keySet()) {

            // Check if a record for this slot.
            if (this.requestRecords.size() - 1 < recordIndex) {
                ItemStack item = this.appendNoPlayerItemStack(inventoryItem);
                this.inventory.item(slot, item);
                this.addAction(slot, () -> {
                });

            } else {
                FriendRequestRecord requestRecord = this.requestRecords.get(recordIndex);
                PlayerRecord playerRecord = playerTable.getFirstRecord(new Query().match("uuid", requestRecord.playerFromUuid));
                assert playerRecord != null;

                String acceptedPlayerName = playerRecord.name;

                // Check that the record still exists in the database.
                FriendRequestRecord result = friendRequestTable.getFirstRecord(new Query().match("uuid", requestRecord.uuid));
                if (result == null) continue;

                Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(UUID.fromString(result.playerFromUuid));
                User friendUser = null;
                if (optionalPlayer.isPresent()) {friendUser = new User(optionalPlayer.get());}

                // Set user to the friend.
                inventoryItem.setUser(friendUser);

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
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);
        PlayerRecord playerRecord = playerTable.getFirstRecord(new Query().match("uuid", record.playerFromUuid));
        assert playerRecord != null;

        // Get name and optional player.
        String name = playerRecord.name;
        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(playerRecord.uuid);
        Player player = null;
        if (optionalPlayer.isPresent()) {
            player = optionalPlayer.get();
        }

        // Parse name.
        String tempName = item.displayName(true);
        tempName = PlaceholderManager.parse(tempName, null, new User(player));
        item.displayName(MessageManager.convert(tempName
                .replace("%name%", name)));

        // Parse lore.
        List<Component> lore = new ArrayList<>();
        for (Object line : item.lore(true)) {
            String tempLine = (String) line;
            tempLine = PlaceholderManager.parse(tempLine, null, new User(player));
            lore.add(MessageManager.convert(tempLine
                    .replace("%name%", name)));
        }
        item.lore(lore, false);

        // Parse nbt.
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
