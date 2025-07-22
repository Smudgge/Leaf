package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.RecordFieldPool;
import org.jetbrains.annotations.NotNull;

public class MessageTable extends Table<MessageRecord> {

    @Override
    public @NotNull String getName() {
        return "Message";
    }

    @Override
    public @NotNull MessageRecord createEmptyRecord(@NotNull RecordFieldPool pool) {
        return new MessageRecord(pool.getString(MessageRecord.UUID_FIELD));
    }
}
