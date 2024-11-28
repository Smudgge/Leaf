package com.github.smuddgge.leaf.user;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ConsoleUser implements User {

    @Override
    public @NotNull UUID getUuid() {
        return null;
    }

    @Override
    public @NotNull String getName() {
        return "";
    }

    @Override
    public @Nullable RegisteredServer getServer() {
        return null;
    }

    @Override
    public long getPing() {
        return 0;
    }

    @Override
    public void sendMessage(@NotNull String message) {

    }

    @Override
    public void sendMessage(@NotNull List<String> messageList) {

    }

    @Override
    public boolean isVanished() {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return false;
    }
}
