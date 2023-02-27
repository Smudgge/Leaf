package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <h1>Represents the friend online inventory</h1>
 * Shows only the friends that are currently online.
 */
public class FriendOnlineInventory extends FriendListInventory {

    /**
     * Used to create a friend online inventory.
     *
     * @param section The instance of the configuration section.
     * @param user    The instance of the user.
     */
    public FriendOnlineInventory(ConfigurationSection section, User user) {
        super(section, user);
        this.overrideRecords();
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
    public FriendOnlineInventory(ConfigurationSection section, User user, String listContextUserUuid) {
        super(section, user, listContextUserUuid);
        this.overrideRecords();
    }

    public void overrideRecords() {
        // Get the list of online friends.
        List<FriendRecord> onlineFriendsList = new ArrayList<>();

        for (FriendRecord friendRecord : this.friendRecords) {
            String uuid = friendRecord.friendPlayerUuid;
            Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(UUID.fromString(uuid));
            if (optionalPlayer.isPresent()) onlineFriendsList.add(friendRecord);
        }

        // Override the friend list.
        this.friendRecords = onlineFriendsList;
    }
}
