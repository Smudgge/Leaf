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
    public String toggleProxyJoin;

    @FieldAnnotation
    public String toggleProxyLeave;

    @FieldAnnotation
    public String toggleServerChange;

    @FieldAnnotation
    public String toggleRequests;

    @FieldAnnotation
    public String toggleMail;

    @FieldAnnotation(fieldKeyType = FieldKeyType.FOREIGN)
    @ForeignKey(tableReferenceName = "Player", tableReferenceValue = "uuid")
    public String playerUuid;
}
