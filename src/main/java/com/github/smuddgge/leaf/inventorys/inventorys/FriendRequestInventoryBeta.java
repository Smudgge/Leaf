package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.FriendRequestManager;
import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendRequestRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendRequestTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.InventoryInterface;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.querz.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class FriendRequestInventoryBeta extends InventoryInterface {

    private final ConfigurationSection section;

    /**
     * Used to create an inventory interface.
     *
     * @param section The list configuration section
     */
    public FriendRequestInventoryBeta(ConfigurationSection section) {
        this.section = section;
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.GENERIC_9X6;
    }

    @Override
    public String getTitle() {
        return "&8&lRequests";
    }

    @Override
    protected void load(User user) {
        if (Leaf.getDatabase().isDisabled()) return;

        FriendRequestTable friendRequestTable = (FriendRequestTable) Leaf.getDatabase().getTable("FriendRequest");
        PlayerTable playerTable = (PlayerTable) Leaf.getDatabase().getTable("Player");

        int index = 0;
        for (Record record : friendRequestTable.getRecord("playerToUuid", user.getUniqueId())) {
            if (index >= 54) return;

            FriendRequestRecord friendRequestRecord = (FriendRequestRecord) record;

            ArrayList<Record> results = playerTable.getRecord("uuid", friendRequestRecord.playerFromUuid);
            if (results.size() == 0) continue;
            PlayerRecord playerFromRecord = (PlayerRecord) results.get(0);

            // Get NBT to set
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("SkullOwner", playerFromRecord.name);

            // Create the item
            ItemStack itemStack = new ItemStack(ItemType.PLAYER_HEAD);

            itemStack.displayName(MessageManager.convertToLegacy(
                    this.section.getString("displayName", "&6&l%name%")
                            .replace("%name%", playerFromRecord.name)));

            for (String item : this.section.getListString("lore")) {
                itemStack.addToLore(MessageManager.convertToLegacy(
                        item.replace("%name%", playerFromRecord.name)
                ));
            }

            itemStack.nbtData(compoundTag);

            this.inventory.item(index, itemStack);

            // When clicked
            this.addAction(index, () -> {
                FriendRequestManager.acceptRequest(friendRequestRecord);

                user.sendMessage(PlaceholderManager.parse(
                        this.section.getString("from", "{message} You are now friends with &f<name>"),
                        null, new User(null, playerFromRecord.name)
                ));

                Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(playerFromRecord.name);
                if (optionalPlayer.isEmpty()) return;

                User userSentTo = new User(optionalPlayer.get());

                userSentTo.sendMessage(PlaceholderManager.parse(
                        this.section.getString("sent", "{message} You are now friends with &f<name>"),
                        null, user
                ));

                for (Map.Entry<Integer, BaseItemStack> entry : this.inventory.items().entrySet()) {
                    this.inventory.removeItem(entry.getKey());
                }

                this.load(user);
            });

            index++;
        }
    }
}