package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.PrimaryFieldMap;
import org.jetbrains.annotations.NotNull;

public class IgnoreTable extends Table<IgnoreRecord> {

    @Override
    public @NotNull String getName() {
        return "Ignore";
    }

    @Override
    public @NotNull IgnoreRecord createEmpty(@NotNull PrimaryFieldMap identifiers) {
        return new IgnoreRecord(identifiers.getString(IgnoreRecord.UUID_FIELD));
    }
}
