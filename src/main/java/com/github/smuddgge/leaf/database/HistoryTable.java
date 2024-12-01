package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.PrimaryFieldMap;
import org.jetbrains.annotations.NotNull;

public class HistoryTable extends Table<HistoryRecord> {

    @Override
    public @NotNull String getName() {
        return "History";
    }

    @Override
    public @NotNull HistoryRecord createEmpty(@NotNull PrimaryFieldMap identifiers) {
        return new HistoryRecord(identifiers.getString(HistoryRecord.UUID_FIELD));
    }
}
