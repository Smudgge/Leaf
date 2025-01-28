package com.github.smuddgge.leaf.user;

import com.github.smuddgge.leaf.Leaf;
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
    public @Nullable String getServerName() {
        return "";
    }

    @Override
    public long getPing() {
        return 0;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        Leaf.get().getLogger().info(
                Leaf.get().getPlaceholderManager().parseLeafPlaceholders(message, null)
        );
    }

    @Override
    public void sendMessage(@NotNull List<String> messageList) {
        for (String message : messageList) {
            this.sendMessage(message);
        }
    }

    @Override
    public boolean isVanished() {
        return false;
    }

    @Override
    public boolean isNotVanishable() {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return false;
    }
}
