package com.github.smuddgge.leaf.inventorys;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.types.Friends;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

import java.util.ArrayList;

public class FriendListInventory extends InventoryInterface {

    /**
     * Used to create an inventory interface.
     *
     * @param user The instance of the user.
     *             This user will be used to load the list of friends.
     */
    public FriendListInventory(User user) {
        super(user);
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.GENERIC_9X6;
    }

    @Override
    protected void load(User user) {
        if (Leaf.getDatabase().isDisabled()) return;

        FriendTable friendTable = (FriendTable) Leaf.getDatabase().getTable("Friend");
        PlayerTable playerTable = (PlayerTable) Leaf.getDatabase().getTable("Player");

        ArrayList<Record> friendRecords = friendTable.getRecord("playerUuid", user.getUniqueId());

        int index = 0;
        for (Record record : friendRecords) {
            FriendRecord friendRecord = (FriendRecord) record;
            PlayerRecord friendPlayerRecord = (PlayerRecord) playerTable.getRecord("uuid", friendRecord.friendPlayerUuid).get(0);

            // Get NBT to set
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("SkullOwner", friendPlayerRecord.name);

            // Create the item
            ItemStack itemStack = new ItemStack(ItemType.PLAYER_HEAD);

            itemStack.displayName(friendRecord.friendNameFormatted);
            itemStack.lore(0, "test");
            itemStack.nbtData(compoundTag);

            this.inventory.item(index, itemStack);

            index ++;
        }
    }
}
