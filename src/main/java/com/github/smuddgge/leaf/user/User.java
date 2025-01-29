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

    @Nullable String getServerName();

    long getPing();

    /**
     * This will also parse placeholders and colors.
     *
     * @param message The message to send to the user.
     */
    void sendMessage(@NotNull String message);

    /**
     * This will also parse placeholders and colors.
     *
     * @param messageList The lines of a message to send to the user.
     */
    void sendMessage(@NotNull List<String> messageList);

    boolean isVanished();

    boolean isNotVanishable();

    boolean hasPermission(@NotNull String permission);

    /**
     * Has every single permission within a
     * list of permissions.
     *
     * @param permissionList
     * @return
     */
    default boolean hasPermission(@NotNull List<String> permissionList) {
        for (String permission : permissionList) {
            if (!this.hasPermission(permission)) return false;
        }
        return true;
    }
}
