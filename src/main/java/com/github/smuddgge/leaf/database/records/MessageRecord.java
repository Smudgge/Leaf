package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.Field;
import com.github.smuddgge.squishydatabase.record.ForeignField;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;

/**
 * Represents a message sent between two players.
 */
public class MessageRecord extends Record {

    @Field(type = RecordFieldType.PRIMARY)
    public String uuid;

    public String message;

    public String date;

    public String timeStamp;

    @Field(type = RecordFieldType.FOREIGN)
    @ForeignField(table = "Player", field = "uuid")
    public String fromPlayerUuid;

    @Field(type = RecordFieldType.FOREIGN)
    @ForeignField(table = "Player", field = "uuid")
    public String toPlayerUuid;
}
