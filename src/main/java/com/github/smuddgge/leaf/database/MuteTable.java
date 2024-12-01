package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.PrimaryFieldMap;
import org.jetbrains.annotations.NotNull;

public class MuteTable extends Table<MuteRecord> {

    @Override
    public @NotNull String getName() {
        return "Mute";
    }

    @Override
    public @NotNull MuteRecord createEmpty(@NotNull PrimaryFieldMap identifiers) {
        return new MuteRecord(identifiers.getString(MuteRecord.UUID_FIELD));
    }
}
