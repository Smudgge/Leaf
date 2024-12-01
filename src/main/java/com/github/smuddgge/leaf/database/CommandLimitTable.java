package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Query;
import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.PrimaryFieldMap;
import net.dv8tion.jda.api.entities.Member;
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

    public @NotNull CommandLimitTable increaseAmountExecuted(@NotNull Member member, String commandId) {

        final @NotNull String id = CommandLimitRecord.createId(member, commandId);

        // Attempt to get the record.
        CommandLimitRecord record = this.getFirstRecord(
                new Query().match(CommandLimitRecord.ID_FIELD, id)
        ).waitAndGet();

        // Has the record not been created?
        if (record == null) {
            record = new CommandLimitRecord(id);
            record.setAmountExecuted(0);
        }

        // Increase the amount it's been executed.
        record.increaseExecutions()
        record.amountExecuted = String.valueOf(record.getAmountExecuted() + 1);

        // Update in database.
        this.insertRecord(record);
    }
}
