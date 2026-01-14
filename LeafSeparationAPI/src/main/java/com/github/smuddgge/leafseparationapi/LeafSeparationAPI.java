package com.github.smuddgge.leafseparationapi;

import com.github.smuddgge.leaf.Metrics;
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.file.Path;

@Plugin(
        id = "leafseparationapi",
        name = "LeafSeparationAPI",
        version = "3.8.0",
        description = "A velocity utility plugin",
        authors = {"Smudge"}
)
public class LeafSeparationAPI {

    @Inject
    public LeafSeparationAPI(ProxyServer server, @DataDirectory final Path folder, Metrics.Factory metricsFactory) {
    }
}
