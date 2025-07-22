package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.RecordFieldPool;
import org.jetbrains.annotations.NotNull;

public class CommandLimitTable extends Table<CommandLimitRecord> {

    @Override
    public @NotNull String getName() {
        return "CommandLimit";
    }

    @Override
    public @NotNull CommandLimitRecord createEmptyRecord(@NotNull RecordFieldPool pool) {
        return new CommandLimitRecord(pool.getString(CommandLimitRecord.ID_FIELD));
    }
}
