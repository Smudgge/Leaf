package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.RecordFieldPool;
import org.jetbrains.annotations.NotNull;

public class IgnoreTable extends Table<IgnoreRecord> {

    @Override
    public @NotNull String getName() {
        return "Ignore";
    }

    @Override
    public @NotNull IgnoreRecord createEmptyRecord(@NotNull RecordFieldPool pool) {
        return new IgnoreRecord(pool.getString(IgnoreRecord.UUID_FIELD));
    }
}
