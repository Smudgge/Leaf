package com.github.smuddgge.leaf.placeholders;

/**
 * Represents placeholder types.
 */
public enum PlaceholderType {
    STANDARD("<", ">"),
    CUSTOM("{", "}");

    private final String prefix;
    private final String suffix;

    /**
     * Used to create a new placeholder type.
     * The prefix and suffix go ether side of the placeholder identifier.
     *
     * @param prefix The prefix of the placeholder.
     * @param suffix The suffix of the placeholder.
     */
    PlaceholderType(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * Used to get the prefix.
     *
     * @return The prefix.
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Used to get the suffix.
     *
     * @return The suffix.
     */
    public String getSuffix() {
        return this.suffix;
    }
}
