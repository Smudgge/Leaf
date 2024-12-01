package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.PrimaryFieldMap;
import org.jetbrains.annotations.NotNull;

public class FriendTable extends Table<FriendRecord> {

    @Override
    public @NotNull String getName() {
        return "Friend";
    }

    @Override
    public @NotNull FriendRecord createEmpty(@NotNull PrimaryFieldMap identifiers) {
        return new FriendRecord(identifiers.getString(FriendRecord.UUID_FIELD));
    }
}
