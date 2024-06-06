package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.Field;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;
import org.jetbrains.annotations.NotNull;

public class CommandCooldownRecord extends Record {

    @Field(type = RecordFieldType.PRIMARY)
    public String primaryKey;
    public String lastExecutedTimeStamp;

    public long getLastExecutedTimeStamp() {
        return Long.parseLong(this.lastExecutedTimeStamp);
    }

    public @NotNull CommandCooldownRecord setLastExecutedTimeStamp(final long lastExecutedTimeStamp) {
        this.lastExecutedTimeStamp = Long.toString(lastExecutedTimeStamp);
        return this;
    }
}
