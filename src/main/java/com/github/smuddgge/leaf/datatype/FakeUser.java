package com.github.smuddgge.leaf.datatype;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

/**
 * Represents a fake user.
 */
public class FakeUser extends User {

    private final RegisteredServer server;

    /**
     * Represents a fake user connected to one of the servers.
     */
    public FakeUser(RegisteredServer server) {
        super(null);
        this.server = server;
    }

    @Override
    public RegisteredServer getConnectedServer() {
        return this.server;
    }

    @Override
    public boolean hasPermission(String permission) {
        return false;
    }
}
