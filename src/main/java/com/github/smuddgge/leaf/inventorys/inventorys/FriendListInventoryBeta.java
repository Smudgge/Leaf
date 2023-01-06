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
import com.github.smuddgge.leaf.inventorys.InventoryInterface;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.querz.nbt.tag.CompoundTag;

import java.util.ArrayList;

public class FriendListInventoryBeta extends InventoryInterface {

    private final ConfigurationSection section;

    /**
     * Used to create an inventory interface.
     *
     * @param section The list configuration section
     */
    public FriendListInventoryBeta(ConfigurationSection section) {
        this.section = section;
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.GENERIC_9X6;
    }

    @Override
    public String getTitle() {
        return "&8&lFriend List";
    }

    @Override
    protected void load(User user) {
        if (Leaf.getDatabase().isDisabled()) return;

        FriendTable friendTable = (FriendTable) Leaf.getDatabase().getTable("Friend");
        FriendMailTable friendMailTable = (FriendMailTable) Leaf.getDatabase().getTable("FriendMail");
        PlayerTable playerTable = (PlayerTable) Leaf.getDatabase().getTable("Player");

        ArrayList<Record> friendRecords = friendTable.getRecord("playerUuid", user.getUniqueId());

        int index = 0;
        for (Record record : friendRecords) {
            if (index >= 54) return;

            FriendRecord friendRecord = (FriendRecord) record;
            PlayerRecord friendPlayerRecord = (PlayerRecord) playerTable.getRecord("uuid", friendRecord.friendPlayerUuid).get(0);
            FriendMailRecord latestMail = friendMailTable.getLatest(user.getUniqueId().toString(), friendPlayerRecord.uuid);

            if (latestMail == null) {
                latestMail = new FriendMailRecord();
                latestMail.message = "none";
                latestMail.viewedBoolean = "false";
            }

            // Get NBT to set
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("SkullOwner", friendPlayerRecord.name);

            // Create the item
            ItemStack itemStack = new ItemStack(ItemType.PLAYER_HEAD);

            itemStack.displayName(MessageManager.convertToLegacy(this.section.getString("displayName", "&6&l%name%")
                    .replace("%name%", friendRecord.friendNameFormatted)
                    .replace("%date%", friendRecord.dateCreated)
                    .replace("%last_mail%", latestMail.message)
                    .replace("%mail_status%", latestMail.getStatus())
            ));

            for (String item : this.section.getListString("lore")) {
                itemStack.addToLore(MessageManager.convertToLegacy(item
                        .replace("%name%", friendRecord.friendNameFormatted)
                        .replace("%date%", friendRecord.dateCreated)
                        .replace("%last_mail%", latestMail.message)
                        .replace("%mail_status%", latestMail.getStatus())
                ));
            }

            itemStack.nbtData(compoundTag);

            this.inventory.item(index, itemStack);

            index++;
        }
    }
}
