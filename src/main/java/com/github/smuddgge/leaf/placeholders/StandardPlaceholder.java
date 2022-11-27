package com.github.smuddgge.leaf.placeholders;

import com.github.smuddgge.leaf.datatype.User;

/**
 * Represents a default placeholder.
 */
public abstract class StandardPlaceholder implements Placeholder {

    @Override
    public PlaceholderType getType() {
        return PlaceholderType.STANDARD;
    }

    @Override
    public abstract String getIdentifier();

    @Override
    public abstract String getValue();

    @Override
    public abstract String getValue(User user);
}
