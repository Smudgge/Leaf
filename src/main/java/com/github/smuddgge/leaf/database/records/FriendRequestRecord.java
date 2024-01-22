package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.Field;
import com.github.smuddgge.squishydatabase.record.ForeignField;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;

public class FriendRequestRecord extends Record {

    @Field(type = RecordFieldType.PRIMARY)
    public String uuid;

    public String dateCreated;

    public String notes;

    @Field(type = RecordFieldType.FOREIGN)
    @ForeignField(table = "Player", field = "uuid")
    public String playerFromUuid;

    @Field(type = RecordFieldType.FOREIGN)
    @ForeignField(table = "Player", field = "uuid")
    public String playerToUuid;
}
