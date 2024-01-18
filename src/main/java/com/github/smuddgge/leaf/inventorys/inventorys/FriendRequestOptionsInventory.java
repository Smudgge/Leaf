package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.FriendManager;
import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.database.records.FriendRequestRecord;
import com.github.smuddgge.leaf.database.tables.FriendRequestTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.dependencys.ProtocolizeDependency;
import com.github.smuddgge.leaf.inventorys.CustomInventory;
import com.github.smuddgge.leaf.inventorys.InventoryItem;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.utility.Sounds;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class FriendRequestOptionsInventory extends CustomInventory {

    private final FriendRequestRecord requestRecord;
    private final String acceptedPlayerName;

    /**
     * Used to create a custom inventory.
     *
     * @param section The parent configuration section to the inventory.
     * @param user    The user that will open the inventory.
     */
    public FriendRequestOptionsInventory(ConfigurationSection section, User user, FriendRequestRecord requestRecord, String acceptedPlayerName) {
        super(section, user, "options");

        this.requestRecord = requestRecord;
        this.acceptedPlayerName = acceptedPlayerName;
    }

    @Override
    public ItemStack onLoadItemWithFunction(InventoryItem inventoryItem) {
        if (Objects.equals(inventoryItem.getFunctionSection().getString("type"), "accept")) {
            for (int slot : inventoryItem.getSlots(this.getInventoryType())) {
                this.addAction(slot, this::accept);
            }
        }
        if (Objects.equals(inventoryItem.getFunctionSection().getString("type"), "deny")) {
            for (int slot : inventoryItem.getSlots(this.getInventoryType())) {
                this.addAction(slot, this::deny);
            }
        }

        return inventoryItem.getItemStack();
    }

    /**
     * Used to deny a request.
     */
    public void deny() {
        if (Leaf.isDatabaseDisabled()) return;

        // Remove the record
        FriendRequestTable friendRequestTable = Leaf.getDatabase().getTable(FriendRequestTable.class);
        friendRequestTable.removeRecord(requestRecord);

        // Re-open the inventory
        new FriendRequestInventory(this.section, this.user).open();
    }

    /**
     * Used to accept the friend request.
     */
    public void accept() {
        if (Leaf.isDatabaseDisabled()) return;

        // Close the inventory to stop friend duplication errors.
        this.close();

        // Accept the friend request.
        FriendManager.acceptRequest(requestRecord);

        // Message the user.
        user.sendMessage(PlaceholderManager.parse(
                this.section.getString("from", "{message} You are now friends with &f<name>"),
                null, new User(null, acceptedPlayerName)
        ));

        // Open a new friend request inventory.
        FriendRequestInventory friendRequestInventory = new FriendRequestInventory(this.section, this.user);
        friendRequestInventory.open();

        // Message the other player if they are online.
        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(acceptedPlayerName);
        if (optionalPlayer.isEmpty()) return;

        User userSentTo = new User(optionalPlayer.get());

        userSentTo.sendMessage(PlaceholderManager.parse(
                this.section.getString("sent", "{message} You are now friends with &f<name>"),
                null, user
        ));
        if (ProtocolizeDependency.isEnabled()) Sounds.play(this.section.getString("accept_sound"), userSentTo.getUniqueId());
    }
}
