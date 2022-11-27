package com.github.smuddgge.leaf.placeholders.standard;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.StandardPlaceholder;

/**
 * Represents the vanished placeholder.
 * Returns if a user is vanished.
 */
public class VanishedPlaceholder extends StandardPlaceholder {

    @Override
    public String getIdentifier() {
        return "vanished";
    }

    @Override
    public String getValue(User user) {
        return String.valueOf(user.isVanished());
    }

    @Override
    public String getValue() {
        return null;
    }
}
