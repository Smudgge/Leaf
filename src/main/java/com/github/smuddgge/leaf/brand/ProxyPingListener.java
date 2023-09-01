package com.github.smuddgge.leaf.brand;

import com.github.smuddgge.leaf.configuration.ConfigMain;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;

/**
 * Used to listen to the proxy ping event and change
 * the brand if enabled.
 */
public class ProxyPingListener {

    @Subscribe
    public void onPing(ProxyPingEvent event) {

        // Check if ping brand is enabled.
        if (!ConfigMain.get().getBoolean("brand.ping.enabled", false)) return;

        // Get ping builder.
        ServerPing.Builder builder = event.getPing().asBuilder();

        // Set server ping version.
        builder.version(
                new ServerPing.Version(
                        event.getPing().getVersion().getProtocol(),
                        ConfigMain.get().getString("brand.ping.brand", "None")
                )
        );

        // Set new ping.
        event.setPing(builder.build());
    }
}
