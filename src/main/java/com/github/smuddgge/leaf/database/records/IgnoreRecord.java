package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.Field;
import com.github.smuddgge.squishydatabase.record.ForeignField;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;

/**
 * Represents an ignored record in the database.
 */
public class IgnoreRecord extends Record {

    @Field(type = RecordFieldType.PRIMARY)
    public String uuid;

    @Field(type = RecordFieldType.FOREIGN)
    @ForeignField(table = "Player", field = "uuid")
    public String playerUuid;

    @Field(type = RecordFieldType.FOREIGN)
    @ForeignField(table = "Player", field = "uuid")
    public String ignoredPlayerUuid;

}
