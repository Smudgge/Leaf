package com.github.smuddgge.leaf.user;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface User {

    @NotNull UUID getUuid();

    @NotNull String getName();

    @Nullable RegisteredServer getServer();

    void sendMessage(@NotNull String message);

    void sendMessage(@NotNull List<String> messageList);

    boolean isVanished();

    boolean hasPermission(@NotNull String permission);

    default boolean hasPermission(@NotNull List<String> permissionList) {
        for (String permission : permissionList) {
            if (!this.hasPermission(permission)) return false;
        }
        return true;
    }
}
