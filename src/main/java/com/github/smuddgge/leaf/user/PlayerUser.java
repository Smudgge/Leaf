package com.github.smuddgge.leaf.user;

import com.github.smuddgge.leaf.Leaf;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PlayerUser implements User {

    private final @NotNull Player player;

    public PlayerUser(@NotNull final Player player) {
        this.player = player;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    @Override
    public @NotNull UUID getUuid() {
        return this.player.getUniqueId();
    }

    @Override
    public @NotNull String getName() {
        return this.player.getUsername();
    }

    @Override
    public @Nullable RegisteredServer getServer() {
        final ServerConnection connection =  this.player.getCurrentServer().orElse(null);
        if (connection == null) return null;
        return connection.getServer();
    }

    @Override
    public @Nullable String getServerName() {
        final RegisteredServer server = this.getServer();
        if (server == null) return null;
        return server.getServerInfo().getName();
    }

    @Override
    public long getPing() {
        return this.player.getPing();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        final Component component = Leaf.get().getPlaceholderManager().parse(message, this.player);
        this.player.sendMessage(component);
    }

    @Override
    public void sendMessage(@NotNull List<String> messageList) {
        final Component component = Leaf.get().getPlaceholderManager().parse(String.join("\n", messageList), this.player);
        this.player.sendMessage(component);
    }

    @Override
    public boolean isVanished() {
        return false;
    }

    @Override
    public boolean isNotVanishable() {
        return !this.hasPermission(Leaf.get().getConfig().getVanishablePermission());
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return false;
    }

    public void increaseAmountExecuted(@NotNull String commandIdentifier) {

    }
}
