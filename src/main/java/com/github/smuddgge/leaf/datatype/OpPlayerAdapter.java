package com.github.smuddgge.leaf.datatype;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.crypto.IdentifiedKey;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.player.PlayerSettings;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import com.velocitypowered.api.proxy.player.TabList;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.ModInfo;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.*;

public class OpPlayerAdapter implements Player {

    private final @NotNull Player player;

    /**
     * Represents an op player.
     *
     * @param player The instance of a player to adapt to.
     */
    public OpPlayerAdapter(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public String getUsername() {
        return this.player.getUsername();
    }

    @Override
    public @Nullable Locale getEffectiveLocale() {
        return this.player.getEffectiveLocale();
    }

    @Override
    public void setEffectiveLocale(Locale locale) {
        this.player.setEffectiveLocale(locale);
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public Optional<ServerConnection> getCurrentServer() {
        return this.player.getCurrentServer();
    }

    @Override
    public PlayerSettings getPlayerSettings() {
        return this.player.getPlayerSettings();
    }

    @Override
    public boolean hasSentPlayerSettings() {
        return this.player.hasSentPlayerSettings();
    }

    @Override
    public Optional<ModInfo> getModInfo() {
        return this.player.getModInfo();
    }

    @Override
    public long getPing() {
        return this.player.getPing();
    }

    @Override
    public boolean isOnlineMode() {
        return this.player.isOnlineMode();
    }

    @Override
    public ConnectionRequestBuilder createConnectionRequest(RegisteredServer server) {
        return this.player.createConnectionRequest(server);
    }

    @Override
    public List<GameProfile.Property> getGameProfileProperties() {
        return this.player.getGameProfileProperties();
    }

    @Override
    public void setGameProfileProperties(List<GameProfile.Property> properties) {
        this.player.setGameProfileProperties(properties);
    }

    @Override
    public GameProfile getGameProfile() {
        return this.player.getGameProfile();
    }

    @Override
    public void clearHeaderAndFooter() {
        this.player.clearHeaderAndFooter();
    }

    @Override
    public void clearPlayerListHeaderAndFooter() {
        this.player.clearPlayerListHeaderAndFooter();
    }

    @Override
    public Component getPlayerListHeader() {
        return this.player.getPlayerListHeader();
    }

    @Override
    public Component getPlayerListFooter() {
        return this.player.getPlayerListFooter();
    }

    @Override
    public TabList getTabList() {
        return this.player.getTabList();
    }

    @Override
    public void disconnect(Component reason) {
        this.player.disconnect(reason);
    }

    @Override
    public void spoofChatInput(String input) {
        this.player.spoofChatInput(input);
    }

    @Override
    public void sendResourcePack(String url) {
        this.player.sendResourcePack(url);
    }

    @Override
    public void sendResourcePack(String url, byte[] hash) {
        this.player.sendResourcePack(url, hash);
    }

    @Override
    public void sendResourcePackOffer(ResourcePackInfo packInfo) {
        this.player.sendResourcePackOffer(packInfo);
    }

    @Override
    public @Nullable ResourcePackInfo getAppliedResourcePack() {
        return this.player.getAppliedResourcePack();
    }

    @Override
    public @Nullable ResourcePackInfo getPendingResourcePack() {
        return this.player.getPendingResourcePack();
    }

    @Override
    public Collection<ResourcePackInfo> getAppliedResourcePacks() {
        return null;
    }

    @Override
    public Collection<ResourcePackInfo> getPendingResourcePacks() {
        return null;
    }

    @Override
    public boolean sendPluginMessage(ChannelIdentifier identifier, byte[] data) {
        return this.player.sendPluginMessage(identifier, data);
    }

    @Override
    public @Nullable String getClientBrand() {
        return this.player.getClientBrand();
    }

    @Override
    public Tristate getPermissionValue(String permission) {
        return Tristate.TRUE;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.player.getRemoteAddress();
    }

    @Override
    public Optional<InetSocketAddress> getVirtualHost() {
        return this.player.getVirtualHost();
    }

    @Override
    public boolean isActive() {
        return this.player.isActive();
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return this.player.getProtocolVersion();
    }

    @Override
    public @Nullable IdentifiedKey getIdentifiedKey() {
        return this.player.getIdentifiedKey();
    }

    @Override
    public @NotNull Identity identity() {
        return this.player.identity();
    }
}
