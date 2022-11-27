package com.github.smuddgge.leaf.placeholders.standard;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.StandardPlaceholder;

/**
 * Represents the server placeholder.
 * Returns what server a user is on.
 */
public class ServerPlaceholder extends StandardPlaceholder {

    @Override
    public String getIdentifier() {
        return "server";
    }

    @Override
    public String getValue(User user) {
        if (user == null) return null;
        return user.getConnectedServer().getServerInfo().getName();
    }

    @Override
    public String getValue() {
        return null;
    }
}
