package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.leaf.database.FieldAnnotation;
import com.github.smuddgge.leaf.database.FieldKeyType;
import com.github.smuddgge.leaf.database.ForeignKey;
import com.github.smuddgge.leaf.database.Record;

/**
 * Represents a history record in the database table.
 */
public class HistoryRecord extends Record {

    @FieldAnnotation(fieldKeyType = FieldKeyType.PRIMARY)
    public String uuid;

    @FieldAnnotation()
    public String server;

    @FieldAnnotation()
    public String date;

    @FieldAnnotation()
    public String timeStamp;

    @FieldAnnotation()
    public String event;

    @FieldAnnotation(fieldKeyType = FieldKeyType.FOREIGN)
    @ForeignKey(tableReferenceName = "Player", tableReferenceValue = "uuid")
    public String playerUuid;
}
