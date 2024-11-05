package com.github.smuddgge.leaf.brand;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configurationold.ConfigMain;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.exception.LeafException;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
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
 * The brand plugin message hook.
 */
class BrandPluginMessageHook extends PluginMessage {

    protected static MethodHandle SERVER_CONNECTION_FIELD;

    @Override
    public boolean handle(MinecraftSessionHandler handler) {

        // Check if the handler is the correct handler.
        // Check if the brand feature is enabled.
        if (!(handler instanceof BackendPlaySessionHandler)
                || !ConfigMain.get().getBoolean("brand.in_game.enabled", false)
                || !PluginMessageUtil.isMcBrand(this)) {

            return super.handle(handler);
        }

        // Check if the version is higher than 3.3
        if (Leaf.getServer().getVersion().getVersion().matches("3|4|5|6|7\\.[3456789]")) {
            return super.handle(handler);
        }

        try {

            // Get the instance of the player.
            VelocityServerConnection connection = (VelocityServerConnection) SERVER_CONNECTION_FIELD.invoke(handler);
            ConnectedPlayer player = connection.getPlayer();

            // Write the minecraft brand.
            player.getConnection().write(
                    this.getMinecraftBrand(this, player)
            );

            return true;

        } catch (Throwable exception) {
            throw new LeafException(exception.getMessage());
        }
    }

    /**
     * Used to get the minecraft brand message.
     *
     * @param message The instance of the current message to rewrite.
     * @param player  The instance of the player.
     * @return The instance of the new message.
     */
    private @NotNull PluginMessage getMinecraftBrand(@NotNull PluginMessage message, @NotNull Player player) {

        // Get the current brand.
        String currentBrand = PluginMessageUtil.readBrandMessage(message.content());

        // Get the brand.
        String brand = PlaceholderManager.parse(
                ConfigMain.get().getString("brand.in_game.brand", "None"),
                null,
                new User(player)
        );

        // Get the new brand.
        String rewrittenBrand = MessageFormat.format(MessageManager.convertToLegacy(brand), currentBrand);

        // Create the buffer.
        ByteBuf rewrittenBuf = Unpooled.buffer();

        // Check if the minecraft version is above or equal to 1.8
        if (player.getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_8) >= 0) {
            ProtocolUtils.writeString(rewrittenBuf, rewrittenBrand);
            return new PluginMessage(message.getChannel(), rewrittenBuf);
        }

        // Otherwise the minecraft version is below.
        rewrittenBuf.writeCharSequence(rewrittenBrand, StandardCharsets.UTF_8);
        return new PluginMessage(message.getChannel(), rewrittenBuf);
    }

    /**
     * Used to get a new instance of this hook.
     *
     * @return The new instance.
     */
    public Supplier<MinecraftPacket> getHook() {
        return BrandPluginMessageHook::new;
    }

    /**
     * Used to get this type of hook.
     *
     * @return The class type.
     */
    public Class<? extends MinecraftPacket> getType() {
        return PluginMessage.class;
    }

    /**
     * Used to get the instance of this class.
     *
     * @return The class instance.
     */
    public Class<? extends MinecraftPacket> getHookClass() {
        return this.getClass();
    }
}
