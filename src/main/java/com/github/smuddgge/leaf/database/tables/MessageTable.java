package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.records.MessageRecord;
import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the message table.
 */
public class MessageTable extends TableAdapter<MessageRecord> {

    @Override
    public @NotNull String getName() {
        return "Message";
    }
}
