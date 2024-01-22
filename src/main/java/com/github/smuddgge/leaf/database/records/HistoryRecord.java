package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.Field;
import com.github.smuddgge.squishydatabase.record.ForeignField;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;

/**
 * Represents a history record in the database table.
 */
public class HistoryRecord extends Record {

    @Field(type = RecordFieldType.PRIMARY)
    public String uuid;

    public String server;

    public String date;

    public String timeStamp;

    public String event;

    @Field(type = RecordFieldType.FOREIGN)
    @ForeignField(table = "Player", field = "uuid")
    public String playerUuid;
}
