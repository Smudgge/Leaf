package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.PrimaryFieldMap;
import org.jetbrains.annotations.NotNull;

public class CommandCooldownTable extends Table<CommandCooldownRecord> {

    @Override
    public @NotNull String getName() {
        return "CommandCooldown";
    }

    @Override
    public @NotNull CommandCooldownRecord createEmpty(@NotNull PrimaryFieldMap identifiers) {
        return new CommandCooldownRecord(identifiers.getString(CommandCooldownRecord.ID_FIELD));
    }
}
