package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.Field;
import com.github.smuddgge.squishydatabase.record.ForeignField;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;

/**
 * Represents a friend record in the database table.
 */
public class FriendRecord extends Record {

    @Field(type = RecordFieldType.PRIMARY)
    public String uuid;

    public String timeStampCreated;

    public String dateCreated;

    public String staredBoolean;

    public String friendNameFormatted;

    public String friendNotes;

    public String friendMeta;

    public String toggleProxyJoin;

    public String toggleProxyLeave;

    public String toggleServerChange;

    @Field(type = RecordFieldType.FOREIGN)
    @ForeignField(table = "Player", field = "uuid")
    public String playerUuid;

    @Field(type = RecordFieldType.FOREIGN)
    @ForeignField(table = "Player", field = "uuid")
    public String friendPlayerUuid;

    /**
     * Used to get if this friend is stared.
     *
     * @return True if stared.
     */
    public boolean isStared() {
        return Boolean.parseBoolean(this.staredBoolean);
    }
}
