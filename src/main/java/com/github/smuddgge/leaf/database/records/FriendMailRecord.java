package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.ForeignKeyAnnotation;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldAnnotation;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;

public class FriendMailRecord extends Record {

    @RecordFieldAnnotation(type = RecordFieldType.PRIMARY)
    public String uuid;

    public String message;

    public String viewedBoolean;

    public String sentDate;

    public String readDate;

    @RecordFieldAnnotation(type = RecordFieldType.FOREIGN)
    @ForeignKeyAnnotation(table = "Player", field = "uuid")
    public String friendFromUuid;

    @RecordFieldAnnotation(type = RecordFieldType.FOREIGN)
    @ForeignKeyAnnotation(table = "Player", field = "uuid")
    public String friendToUuid;

    /**
     * Used to get the status of the mail.
     *
     * @return viewed or sent.
     */
    public String getStatus() {
        if (Boolean.parseBoolean(viewedBoolean)) return "viewed";
        return "sent";
    }
}
