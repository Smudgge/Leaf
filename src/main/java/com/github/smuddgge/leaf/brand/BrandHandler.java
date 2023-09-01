package com.github.smuddgge.leaf.brand;

import com.github.smuddgge.leaf.Leaf;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.backend.BackendPlaySessionHandler;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.network.ConnectionManager;
import com.velocitypowered.proxy.network.ServerChannelInitializerHolder;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.StateRegistry;
import io.netty.channel.ChannelInitializer;
import io.netty.util.collection.IntObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.nio.channels.Channel;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Represents the brand handler.
 */
public final class BrandHandler {

    /**
     * Used to set up the brand handler.
     */
    public static void setup() {
        // Attempt to hook brand message hook.
        try {

            MethodHandle cm = MethodHandles.privateLookupIn(Leaf.getServer().getClass(), MethodHandles.lookup())
                    .findGetter(Leaf.getServer().getClass(), "cm", ConnectionManager.class);

            ServerChannelInitializerHolder serverChannelInitializer = ((ConnectionManager) cm.invoke(Leaf.getServer())).getServerChannelInitializer();
            Field initializerField = serverChannelInitializer.getClass().getDeclaredField("initializer");
            initializerField.setAccessible(true);
            ChannelInitializer<Channel> initializer = (ChannelInitializer<Channel>) initializerField.get(serverChannelInitializer);
            initializerField.set(serverChannelInitializer, new ChannelInitializerHook(initializer));

            BrandPluginMessageHook.SERVER_CONNECTION_FIELD = MethodHandles
                    .privateLookupIn(BackendPlaySessionHandler.class, MethodHandles.lookup())
                    .findGetter(BackendPlaySessionHandler.class, "serverConn", VelocityServerConnection.class);

            MethodHandle versionsField = MethodHandles.privateLookupIn(StateRegistry.PacketRegistry.class, MethodHandles.lookup())
                    .findGetter(StateRegistry.PacketRegistry.class, "versions", Map.class);

            MethodHandle packetIdToSupplierField = MethodHandles
                    .privateLookupIn(StateRegistry.PacketRegistry.ProtocolRegistry.class, MethodHandles.lookup())
                    .findGetter(StateRegistry.PacketRegistry.ProtocolRegistry.class, "packetIdToSupplier", IntObjectMap.class);

            MethodHandle packetClassToIdField = MethodHandles
                    .privateLookupIn(StateRegistry.PacketRegistry.ProtocolRegistry.class, MethodHandles.lookup())
                    .findGetter(StateRegistry.PacketRegistry.ProtocolRegistry.class, "packetClassToId", Object2IntMap.class);

            // Create new hook.
            BiConsumer<? super ProtocolVersion, ? super StateRegistry.PacketRegistry.ProtocolRegistry> consumer
                    = getBiConsumer(packetIdToSupplierField, packetClassToIdField);

            MethodHandle clientsideGetter = MethodHandles.privateLookupIn(StateRegistry.class, MethodHandles.lookup())
                    .findGetter(StateRegistry.class, "clientbound", StateRegistry.PacketRegistry.class);

            MethodHandle serversideGetter = MethodHandles.privateLookupIn(StateRegistry.class, MethodHandles.lookup())
                    .findGetter(StateRegistry.class, "serverbound", StateRegistry.PacketRegistry.class);

            StateRegistry.PacketRegistry playClientbound = (StateRegistry.PacketRegistry) clientsideGetter.invokeExact(StateRegistry.PLAY);
            StateRegistry.PacketRegistry handshakeServerbound = (StateRegistry.PacketRegistry) serversideGetter.invokeExact(StateRegistry.HANDSHAKE);

            ((Map<ProtocolVersion, StateRegistry.PacketRegistry.ProtocolRegistry>) versionsField.invokeExact(playClientbound)).forEach(consumer);
            ((Map<ProtocolVersion, StateRegistry.PacketRegistry.ProtocolRegistry>) versionsField.invokeExact(handshakeServerbound)).forEach(consumer);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull BiConsumer<? super ProtocolVersion, ? super StateRegistry.PacketRegistry.ProtocolRegistry> getBiConsumer(
            MethodHandle packetIdToSupplierField,
            MethodHandle packetClassToIdField) {

        BrandPluginMessageHook hook = new BrandPluginMessageHook();

        BiConsumer<? super ProtocolVersion, ? super StateRegistry.PacketRegistry.ProtocolRegistry> consumer = (version, registry) -> {
            try {
                IntObjectMap<Supplier<? extends MinecraftPacket>> packetIdToSupplier
                        = (IntObjectMap<Supplier<? extends MinecraftPacket>>) packetIdToSupplierField.invoke(registry);

                Object2IntMap<Class<? extends MinecraftPacket>> packetClassToId
                        = (Object2IntMap<Class<? extends MinecraftPacket>>) packetClassToIdField.invoke(registry);

                int packetId = packetClassToId.getInt(hook.getType());
                packetClassToId.put(hook.getHookClass(), packetId);
                packetIdToSupplier.remove(packetId);
                packetIdToSupplier.put(packetId, hook.getHook());

            } catch (Throwable exception) {
                throw new RuntimeException(exception);
            }
        };
        return consumer;
    }
}
