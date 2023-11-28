package com.github.smuddgge.leaf.brand;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.configuration.ConfigMain;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.event.AwaitingEventExecutor;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;

/**
 * Used to listen to the proxy ping event and change
 * the brand if enabled.
 */
public class BrandProxyPingListener implements AwaitingEventExecutor<ProxyPingEvent> {

    /**
     * Used to register this event.
     *
     * @param plugin       The instance of the plugin.
     * @param eventManager The instance of the event manager to register with.
     */
    public void register(Leaf plugin, EventManager eventManager) {
        eventManager.register(plugin, ProxyPingEvent.class, this);
    }

    @Override
    public EventTask executeAsync(ProxyPingEvent event) {
        return EventTask.async(() -> {
            // Check if ping brand is enabled.
            if (!ConfigMain.get().getBoolean("brand.ping.enabled", false)) return;

            // Get ping builder.
            final ServerPing.Builder builder = event.getPing().asBuilder();

            // Set server ping version.
            builder.version(
                    new ServerPing.Version(
                            event.getPing().getVersion().getProtocol(),
                            PlaceholderManager.parse(ConfigMain.get().getString("brand.ping.brand", "None"))
                    )
            );

            // Set new ping.
            event.setPing(builder.build());
        });
    }
}
