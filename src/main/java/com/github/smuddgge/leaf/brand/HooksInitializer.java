/*
 * Copyright (C) 2021 - 2023 Elytrium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.smuddgge.leaf.brand;

import com.github.smuddgge.leaf.Leaf;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.connection.backend.BackendPlaySessionHandler;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.network.ConnectionManager;
import com.velocitypowered.proxy.network.ServerChannelInitializerHolder;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.StateRegistry;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.util.collection.IntObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.elytrium.java.commons.reflection.ReflectionException;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author hevav
 */
public class HooksInitializer {

  /**
   * Used to initialize the brand changer.
   */
  @SuppressWarnings("unchecked")
  public static void init() {
    try {

      // Get the server connection field.
      PluginMessageHook.SERVER_CONNECTION_FIELD = MethodHandles
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

      // Create a new plugin message hook.
      BiConsumer<? super ProtocolVersion, ? super StateRegistry.PacketRegistry.ProtocolRegistry> consumer
              = HooksInitializer.getConsumer(packetIdToSupplierField, packetClassToIdField);

      MethodHandle clientsideGetter = MethodHandles.privateLookupIn(StateRegistry.class, MethodHandles.lookup())
              .findGetter(StateRegistry.class, "clientbound", StateRegistry.PacketRegistry.class);

      StateRegistry.PacketRegistry playClientside = (StateRegistry.PacketRegistry) clientsideGetter.invokeExact(StateRegistry.PLAY);

      ((Map<ProtocolVersion, StateRegistry.PacketRegistry.ProtocolRegistry>) versionsField.invokeExact(playClientside)).forEach(consumer);

    } catch (Throwable e) {
      throw new ReflectionException(e);
    }
  }

  @NotNull
  private static BiConsumer<? super ProtocolVersion, ? super StateRegistry.PacketRegistry.ProtocolRegistry> getConsumer(MethodHandle packetIdToSupplierField, MethodHandle packetClassToIdField) {
    PluginMessageHook hook = new PluginMessageHook();

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
        exception.printStackTrace();
      }
    };
    return consumer;
  }
}
