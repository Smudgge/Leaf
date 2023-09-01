package com.github.smuddgge.leaf.brand;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

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
  }
}
