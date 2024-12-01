package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.PrimaryFieldMap;
import org.jetbrains.annotations.NotNull;

public class CommandLimitTable extends Table<CommandLimitRecord> {

    @Override
    public @NotNull String getName() {
        return "CommandLimit";
    }

    @Override
    public @NotNull CommandLimitRecord createEmpty(@NotNull PrimaryFieldMap identifiers) {
        return new CommandLimitRecord(identifiers.getString(CommandLimitRecord.ID_FIELD));
    }
}
