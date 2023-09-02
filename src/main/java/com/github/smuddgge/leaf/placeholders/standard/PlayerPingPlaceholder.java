package com.github.smuddgge.leaf.placeholders.standard;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.StandardPlaceholder;

/**
 * Used to get the players ping.
 */
public class PlayerPingPlaceholder extends StandardPlaceholder {

    @Override
    public String getIdentifier() {
        return "ping";
    }

    @Override
    public String getValue(User user) {
        return String.valueOf(user.getPing());
    }

    @Override
    public String getValue() {
        return "0";
    }
}
