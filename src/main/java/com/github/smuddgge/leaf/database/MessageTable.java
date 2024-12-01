package com.github.smuddgge.leaf.database;

import com.github.squishylib.database.Table;
import com.github.squishylib.database.field.PrimaryFieldMap;
import org.jetbrains.annotations.NotNull;

public class MessageTable extends Table<MessageRecord> {

    @Override
    public @NotNull String getName() {
        return "Message";
    }

    @Override
    public @NotNull MessageRecord createEmpty(@NotNull PrimaryFieldMap identifiers) {
        return new MessageRecord(identifiers.getString(MessageRecord.UUID_FIELD));
    }
}
