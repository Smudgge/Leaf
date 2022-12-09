package com.github.smuddgge.leaf.database;

/**
 * <h2>A fields value type</h2>
 * Used to specify what type of value the field is
 * so databases such as sqlite get identify what type the field is.
 */
public enum FieldValueType {
    STRING, INTEGER;

    /**
     * Used to get a field value type from a class instance
     *
     * @param instance Instance of a class
     * @return Null if it is not a field value type
     */
    public static FieldValueType get(Class<?> instance) {
        if (instance.isAssignableFrom(String.class)) return FieldValueType.STRING;
        if (instance.isAssignableFrom(Integer.class)) return FieldValueType.INTEGER;
        return null;
    }
}
