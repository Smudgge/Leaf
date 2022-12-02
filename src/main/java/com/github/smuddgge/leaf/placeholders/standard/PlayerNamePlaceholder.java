package com.github.smuddgge.leaf.placeholders.standard;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.StandardPlaceholder;

/**
 * Represents the player name placeholder.
 * Returns the players name.
 */
public class PlayerNamePlaceholder extends StandardPlaceholder {

    @Override
    public String getIdentifier() {
        return "player";
    }

    @Override
    public String getValue(User user) {
        return (String) user.getName();
    }

    @Override
    public String getValue() {
        return null;
    }
}
