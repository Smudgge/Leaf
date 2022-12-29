package com.github.smuddgge.leaf.inventorys;

import com.github.smuddgge.leaf.FriendRequestManager;
import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendMailRecord;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.records.FriendRequestRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendMailTable;
import com.github.smuddgge.leaf.database.tables.FriendRequestTable;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.querz.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.Optional;

public class FriendRequestInventory extends InventoryInterface {

    private final ConfigurationSection section;

    /**
     * Used to create an inventory interface.
     *
     * @param user    The instance of the user.
     *                This user will be used to load the list of friends.
     * @param section The list configuration section
     */
    public FriendRequestInventory(User user, ConfigurationSection section) {
        super(user);

        this.section = section;
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.GENERIC_9X6;
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
            System.out.println("create");

            itemStack.displayName(this.section.getString("displayName", "&6&l%name%")
                    .replace("%name%", playerFromRecord.name));
            System.out.println("displayname");

            for (String item : this.section.getListString("lore")) {
                itemStack.addToLore(item
                        .replace("%name%", playerFromRecord.name)
                );
            }

            System.out.println("lore");

            itemStack.nbtData(compoundTag);

            this.inventory.item(index, itemStack);
            System.out.println(index);
            System.out.println("tag");

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
            });

            index ++;
        }
    }
}