package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.leaf.database.FieldAnnotation;
import com.github.smuddgge.leaf.database.FieldKeyType;
import com.github.smuddgge.leaf.database.ForeignKey;
import com.github.smuddgge.leaf.database.Record;

public class HistoryRecord extends Record {

    @FieldAnnotation(fieldKeyType = FieldKeyType.PRIMARY)
    public String uuid;

    @FieldAnnotation(fieldKeyType = FieldKeyType.FOREIGN)
    @ForeignKey(tableReferenceName = "Player", tableReferenceValue = "uuid")
    public String playerUuid;

    @FieldAnnotation()
    public String server;

    @FieldAnnotation()
    public String date;

    @FieldAnnotation()
    public String event;
}
