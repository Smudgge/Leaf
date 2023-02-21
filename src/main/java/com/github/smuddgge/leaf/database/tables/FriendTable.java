package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FriendTable extends TableAdapter<FriendRecord> {

    @Override
    public @NotNull String getName() {
        return "Friend";
    }

    /**
     * Used to get a friend record given the two players.
     *
     * @param playerUuid The players uuid.
     * @param friendUuid The player friends uuid.
     * @return The requested friend record.
     */
    public @Nullable FriendRecord getFriend(String playerUuid, String friendUuid) {
        return this.getFirstRecord(new Query()
                .match("playerUuid", playerUuid)
                .match("friendPlayerUuid", friendUuid)
        );
    }

    /**
     * Used to get a players friend list.
     *
     * @param playerUuid The players uuid.
     * @return The requested list of friend records.
     */
    public List<FriendRecord> getFriendList(String playerUuid) {
        return this.getRecordList(new Query()
                .match("playerUuid", playerUuid)
        );
    }
}
