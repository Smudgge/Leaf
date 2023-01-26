package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.leaf.database.FieldAnnotation;
import com.github.smuddgge.leaf.database.FieldKeyType;
import com.github.smuddgge.leaf.database.ForeignKey;
import com.github.smuddgge.leaf.database.Record;

/**
 * Represents a players friend settings. This is global for every friend.
 */
public class FriendSettingsRecord extends Record {

    @FieldAnnotation(fieldKeyType = FieldKeyType.PRIMARY)
    public String uuid;

    @FieldAnnotation
    public String toggleProxyJoin = "false";

    @FieldAnnotation
    public String toggleProxyLeave = "false";

    @FieldAnnotation
    public String toggleServerChange = "false";

    @FieldAnnotation
    public String toggleRequests = "true";

    @FieldAnnotation
    public String toggleMail = "true";

    @FieldAnnotation(fieldKeyType = FieldKeyType.FOREIGN)
    @ForeignKey(tableReferenceName = "Player", tableReferenceValue = "uuid")
    public String playerUuid;
}
