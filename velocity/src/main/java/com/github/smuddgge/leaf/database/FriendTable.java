package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.RecordFieldPool;
import org.jetbrains.annotations.NotNull;

public class FriendTable extends Table<FriendRecord> {

    @Override
    public @NotNull String getName() {
        return "Friend";
    }

    @Override
    public @NotNull FriendRecord createEmptyRecord(@NotNull RecordFieldPool pool) {
        return new FriendRecord(pool.getString(FriendRecord.UUID_FIELD));
    }
}
