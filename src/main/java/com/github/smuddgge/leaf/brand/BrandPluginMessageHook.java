package com.github.smuddgge.leaf.brand;

import com.github.smuddgge.leaf.configuration.ConfigMain;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.connection.backend.BackendPlaySessionHandler;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.PluginMessage;
import com.velocitypowered.proxy.protocol.util.PluginMessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.function.Supplier;

/**
 * Used to change the in game brand.
 *
 * Created with help from net.elytrium.velocitytools
 */
public class BrandPluginMessageHook extends PluginMessage {

    public static MethodHandle SERVER_CONNECTION_FIELD;

    public boolean handle(MinecraftSessionHandler handler) {
        // Check if the handler is the backend play session handler.
        // Check if the in game brand is enabled.
        // Check if this is the mc brand.
        if (!(handler instanceof BackendPlaySessionHandler)
                || !ConfigMain.get().getBoolean("brand.in_game.enabled", false)
                || PluginMessageUtil.isMcBrand(this)) {

            return super.handle(handler);
        }

        try {

            // Get connected player.
            ConnectedPlayer player = ((VelocityServerConnection) SERVER_CONNECTION_FIELD.invoke(handler)).getPlayer();

            // Write the brand.
            player.getConnection().write(this.getMinecraftBrandMessage(player.getProtocolVersion()));
            return true;

        } catch (Throwable exception) {
            throw new RuntimeException(exception);
        }
    }

    private @NotNull PluginMessage getMinecraftBrandMessage(ProtocolVersion protocolVersion) {

        // Get the current brand.
        String currentBrand = PluginMessageUtil.readBrandMessage(this.content());

        // Get new re-written brand.
        String rewrittenBrand = MessageFormat.format(
                ConfigMain.get().getString("brand.in_game.brand", "None"),
                currentBrand
        );

        // Create buffer.
        ByteBuf rewrittenBuf = Unpooled.buffer();

        // Check if the version is greater or equal to 1.8
        if (protocolVersion.compareTo(ProtocolVersion.MINECRAFT_1_8) >= 0) {
            ProtocolUtils.writeString(rewrittenBuf, rewrittenBrand);
            return new PluginMessage(this.getChannel(), rewrittenBuf);
        }

        // Otherwise it is lower than 1.8
        rewrittenBuf.writeCharSequence(rewrittenBrand, StandardCharsets.UTF_8);
        return new PluginMessage(this.getChannel(), rewrittenBuf);
    }

    public Supplier<MinecraftPacket> getHook() {
        return BrandPluginMessageHook::new;
    }

    public Class<? extends MinecraftPacket> getType() {
        return PluginMessage.class;
    }

    public Class<? extends MinecraftPacket> getHookClass() {
        return this.getClass();
    }
}
