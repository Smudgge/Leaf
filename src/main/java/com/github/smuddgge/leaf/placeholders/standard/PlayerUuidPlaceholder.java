package com.github.smuddgge.leaf.placeholders.standard;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.StandardPlaceholder;

/**
 * <h1>Represents the uuid placeholder</h1>
 * Returns the users uuid.
 */
public class PlayerUuidPlaceholder extends StandardPlaceholder {

    @Override
    public String getIdentifier() {
        return "uuid";
    }

    @Override
    public String getValue(User user) {
        if (user == null) return null;

        return user.getUniqueId() == null ? null : user.getUniqueId().toString();
    }

    @Override
    public String getValue() {
        return null;
    }
}
