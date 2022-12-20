package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.leaf.database.FieldAnnotation;
import com.github.smuddgge.leaf.database.FieldKeyType;
import com.github.smuddgge.leaf.database.ForeignKey;
import com.github.smuddgge.leaf.database.Record;

/**
 * Represents a friend record in the database table.
 */
public class FriendRecord extends Record {

    @FieldAnnotation(fieldKeyType = FieldKeyType.PRIMARY)
    public String uuid;

    @FieldAnnotation
    public String timeStampCreated;

    @FieldAnnotation
    public String friendNameFormatted;

    @FieldAnnotation
    public String friendNotes;

    @FieldAnnotation
    public String friendMeta;

    @FieldAnnotation
    public String toggleProxyJoin;

    @FieldAnnotation
    public String toggleProxyLeave;

    @FieldAnnotation
    public String toggleServerChange;

    @FieldAnnotation(fieldKeyType = FieldKeyType.FOREIGN)
    @ForeignKey(tableReferenceName = "Player", tableReferenceValue = "uuid")
    public String playerUuid;

    @FieldAnnotation(fieldKeyType = FieldKeyType.FOREIGN)
    @ForeignKey(tableReferenceName = "Player", tableReferenceValue = "uuid")
    public String friendPlayerUuid;
}
