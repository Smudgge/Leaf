package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.leaf.database.FieldAnnotation;
import com.github.smuddgge.leaf.database.FieldKeyType;
import com.github.smuddgge.leaf.database.ForeignKey;
import com.github.smuddgge.leaf.database.Record;

public class FriendMailRecord extends Record {

    @FieldAnnotation(fieldKeyType = FieldKeyType.PRIMARY)
    public String uuid;

    @FieldAnnotation
    public String message;

    @FieldAnnotation
    public String viewedBoolean;

    @FieldAnnotation
    public String sentDate;

    @FieldAnnotation
    public String readDate;

    @FieldAnnotation(fieldKeyType = FieldKeyType.FOREIGN)
    @ForeignKey(tableReferenceName = "Friend", tableReferenceValue = "uuid")
    public String friendFromUuid;

    @FieldAnnotation(fieldKeyType = FieldKeyType.FOREIGN)
    @ForeignKey(tableReferenceName = "Friend", tableReferenceValue = "uuid")
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
