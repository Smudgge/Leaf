package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.Field;
import com.github.smuddgge.squishydatabase.record.ForeignField;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;

public class FriendMailRecord extends Record {

    @Field(type = RecordFieldType.PRIMARY)
    public String uuid;

    public String message;

    public String viewedBoolean;

    public String sentDate;

    public String readDate;

    @Field(type = RecordFieldType.FOREIGN)
    @ForeignField(table = "Player", field = "uuid")
    public String friendFromUuid;

    @Field(type = RecordFieldType.FOREIGN)
    @ForeignField(table = "Player", field = "uuid")
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
