package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.PrimaryFieldMap;
import org.jetbrains.annotations.NotNull;

public class PlayerTable extends Table<PlayerRecord> {

    @Override
    public @NotNull String getName() {
        return "Player";
    }

    @Override
    public @NotNull PlayerRecord createEmpty(@NotNull PrimaryFieldMap identifiers) {
        return new PlayerRecord(identifiers.getString(PlayerRecord.UUID_FIELD));
    }
}
