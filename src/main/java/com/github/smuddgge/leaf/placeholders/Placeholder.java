package com.github.smuddgge.leaf.placeholders;

import com.github.smuddgge.leaf.datatype.User;

/**
 * Represents a placeholder.
 */
public interface Placeholder {

    /**
     * Used to get the placeholder's type.
     *
     * @return The type of placeholder.
     */
    PlaceholderType getType();

    /**
     * Used to get the placeholder's identifier.
     * <ul>
     *     <li>The identifier is the name of the placeholder
     *     located inside the surrounding prefix and suffix.</li>
     * </ul>
     *
     * @return The identifier.
     */
    String getIdentifier();

    /**
     * Used to get the value of the placeholder in context of a user.
     * The value is what the placeholder is replaced with.
     *
     * @param user The instance of the user.
     * @return The placeholders value.
     */
    String getValue(User user);

    /**
     * Used to get the value of the placeholder.
     * The value is what the placeholder is replaced with.
     *
     * @return The placeholders value.
     */
    String getValue();

    /**
     * Used to get the placeholder as a string.
     *
     * @return Placeholder as a string.
     */
    default String getString() {
        return this.getType().getPrefix() + this.getIdentifier() + this.getType().getSuffix();
    }
}
