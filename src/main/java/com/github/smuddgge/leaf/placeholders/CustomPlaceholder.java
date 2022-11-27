package com.github.smuddgge.leaf.placeholders;

import com.github.smuddgge.leaf.datatype.User;

public abstract class CustomPlaceholder implements Placeholder {

    @Override
    public PlaceholderType getType() {
        return PlaceholderType.CUSTOM;
    }

    @Override
    public abstract String getIdentifier();

    @Override
    public abstract String getValue();

    @Override
    public abstract String getValue(User user);
}