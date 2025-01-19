package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.RecordFieldPool;
import org.jetbrains.annotations.NotNull;

public class PlayerTable extends Table<PlayerRecord> {

    @Override
    public @NotNull String getName() {
        return "Player";
    }

    @Override
    public @NotNull PlayerRecord createEmptyRecord(@NotNull RecordFieldPool pool) {
        return new PlayerRecord(pool.getString(PlayerRecord.UUID_FIELD));
    }
}
