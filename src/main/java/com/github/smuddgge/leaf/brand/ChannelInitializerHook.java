package com.github.smuddgge.leaf.brand;

import com.velocitypowered.proxy.network.Connections;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import java.lang.reflect.Method;
import net.elytrium.velocitytools.Settings;
import org.jetbrains.annotations.NotNull;

public class ChannelInitializerHook extends ChannelInitializer<Channel> {

    private final Method initChannel;
    private final ChannelInitializer<Channel> originalInitializer;

    public ChannelInitializerHook(ChannelInitializer<Channel> originalInitializer) {
        try {
            this.initChannel = ChannelInitializer.class.getDeclaredMethod("initChannel", Channel.class);
            this.initChannel.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        this.originalInitializer = originalInitializer;
    }

    @Override
    protected void initChannel(@NotNull Channel channel) throws Exception {
        this.initChannel.invoke(this.originalInitializer, channel);
        ChannelPipeline pipeline = channel.pipeline();
        if (Settings.IMP.TOOLS.DISABLE_LEGACY_PING && pipeline.names().contains(Connections.LEGACY_PING_DECODER)) {
            pipeline.remove(Connections.LEGACY_PING_DECODER);
        }
    }
}