package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.ForeignKeyAnnotation;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldAnnotation;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;

/**
 * Represents a history record in the database table.
 */
public class HistoryRecord extends Record {

    @RecordFieldAnnotation(type = RecordFieldType.PRIMARY)
    public String uuid;

    public String server;

    public String date;

    public String timeStamp;

    public String event;

    @RecordFieldAnnotation(type = RecordFieldType.FOREIGN)
    @ForeignKeyAnnotation(table = "Player", field = "uuid")
    public String playerUuid;
}
