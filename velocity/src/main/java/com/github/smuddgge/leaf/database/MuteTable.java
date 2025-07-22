package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.RecordFieldPool;
import org.jetbrains.annotations.NotNull;

public class MuteTable extends Table<MuteRecord> {

    @Override
    public @NotNull String getName() {
        return "Mute";
    }

    @Override
    public @NotNull MuteRecord createEmptyRecord(@NotNull RecordFieldPool pool) {
        return new MuteRecord(pool.getString(MuteRecord.UUID_FIELD));
    }
}
