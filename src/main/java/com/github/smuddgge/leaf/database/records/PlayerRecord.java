package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.Field;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;

/**
 * Represents a players record in the database table.
 */
public class PlayerRecord extends Record {

    @Field(type = RecordFieldType.PRIMARY)
    public String uuid;

    public String name;

    /**
     * Added at version 2.2.0
     */
    public String toggleCanMessage = null;

    public String toggleSeeSpy = null;

    /**
     * Added at version 3.0.0
     */
    public String variables;
}
