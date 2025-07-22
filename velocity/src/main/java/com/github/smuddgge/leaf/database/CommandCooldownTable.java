package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.RecordFieldPool;
import org.jetbrains.annotations.NotNull;

public class CommandCooldownTable extends Table<CommandCooldownRecord> {

    @Override
    public @NotNull String getName() {
        return "CommandCooldown";
    }

    @Override
    public @NotNull CommandCooldownRecord createEmptyRecord(@NotNull RecordFieldPool pool) {
        return new CommandCooldownRecord(pool.getString(CommandCooldownRecord.ID_FIELD));
    }
}
