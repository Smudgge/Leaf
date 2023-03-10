package com.github.smuddgge.leaf.placeholders.standard;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.StandardPlaceholder;

/**
 * <h1>Represents the server placeholder</h1>
 * Returns what server a user is on without checking if they are vanished.
 */
public class ServerPlaceholder extends StandardPlaceholder {

    @Override
    public String getIdentifier() {
        return "server";
    }

    @Override
    public String getValue(User user) {
        if (user == null) return null;
        if (user.getConnectedServer() == null) return null;

        return user.getConnectedServer().getServerInfo().getName();
    }

    @Override
    public String getValue() {
        return null;
    }
}
