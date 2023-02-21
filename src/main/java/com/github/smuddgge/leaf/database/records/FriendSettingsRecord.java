package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.ForeignKeyAnnotation;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldAnnotation;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;

/**
 * Represents a players friend settings. This is global for every friend.
 */
public class FriendSettingsRecord extends Record {

    @RecordFieldAnnotation(type = RecordFieldType.PRIMARY)
    public String uuid;

    public String toggleProxyJoin = "false";

    public String toggleProxyLeave = "false";

    public String toggleServerChange = "false";

    public String toggleRequests = "true";

    public String toggleMail = "true";

    @RecordFieldAnnotation(type = RecordFieldType.FOREIGN)
    @ForeignKeyAnnotation(table = "Player", field = "uuid")
    public String playerUuid;
}
