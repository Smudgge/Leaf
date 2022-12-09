package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.leaf.database.FieldAnnotation;
import com.github.smuddgge.leaf.database.FieldKeyType;
import com.github.smuddgge.leaf.database.Record;

/**
 * Represents a players record in the database table.
 */
public class PlayerRecord extends Record {

    @FieldAnnotation(fieldKeyType = FieldKeyType.PRIMARY)
    public String uuid;

    @FieldAnnotation()
    public String name;
}
