package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.records.FriendMailRecord;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import org.jetbrains.annotations.NotNull;

public class FriendMailTable extends TableAdapter<FriendMailRecord> {

    @Override
    public @NotNull String getName() {
        return "FriendMail";
    }

    /**
     * Used to get the latest mail sent.
     *
     * @param fromUuid The uuid of the player sending the mail.
     * @param toUuid   The uuid of the player the mail was sent to.
     * @return Null if there are none.
     */
    public FriendMailRecord getLatest(String fromUuid, String toUuid) {
        return this.getFirstRecord(new Query()
                .match("friendFromUuid", fromUuid)
                .match("friendToUuid", toUuid)
        );
    }
}
