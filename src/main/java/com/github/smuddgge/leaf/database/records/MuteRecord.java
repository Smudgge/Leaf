package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldAnnotation;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;

/**
 * Represents a mute record.
 */
public class MuteRecord extends Record {

    @RecordFieldAnnotation(type = RecordFieldType.PRIMARY)
    public String uuid;

    /**
     * When the mute was created.
     */
    public String timeStampCreate;

    /**
     * When the mute wil end.
     * -1 = forever.
     */
    public String timeStampEnd;
}
