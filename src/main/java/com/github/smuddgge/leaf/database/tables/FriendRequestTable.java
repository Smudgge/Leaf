package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.records.FriendRequestRecord;
import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import org.jetbrains.annotations.NotNull;

public class FriendRequestTable extends TableAdapter<FriendRequestRecord> {

    @Override
    public @NotNull String getName() {
        return "FriendRequest";
    }
}
