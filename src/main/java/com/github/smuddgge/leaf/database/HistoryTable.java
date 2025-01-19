package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.RecordFieldPool;
import org.jetbrains.annotations.NotNull;

public class HistoryTable extends Table<HistoryRecord> {

    @Override
    public @NotNull String getName() {
        return "History";
    }

    @Override
    public @NotNull HistoryRecord createEmptyRecord(@NotNull RecordFieldPool pool) {
        return new HistoryRecord(pool.getString(HistoryRecord.UUID_FIELD));
    }
}
