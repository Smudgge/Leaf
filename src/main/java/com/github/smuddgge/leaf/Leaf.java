package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.placeholders.ConditionManager;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.placeholders.conditions.MatchCondition;
import com.github.smuddgge.leaf.placeholders.conditions.PermissionCondition;
import com.github.smuddgge.leaf.placeholders.standard.ServerPlaceholder;
import com.github.smuddgge.leaf.placeholders.standard.VersionPlaceholder;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "leaf",
        name = "Leaf",
        version = "1.0.0",
        description = "A velocity utility plugin",
        authors = {"Smudge"}
)
public class Leaf {

    private static ProxyServer server;
    private static Logger logger;

    @Inject
    public void SmUtility(ProxyServer server, Logger logger, @DataDirectory final Path folder) {
        Leaf.server = server;
        Leaf.logger = logger;

        // Setup configuration files
        ConfigCommands.initialise(folder.toFile());
        ConfigMessages.initialise(folder.toFile());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        // Register placeholders
        PlaceholderManager.register(new ServerPlaceholder());
        PlaceholderManager.register(new VersionPlaceholder());
        PlaceholderManager.register(new VersionPlaceholder());

        // Register placeholder conditions
        ConditionManager.register(new MatchCondition());
        ConditionManager.register(new PermissionCondition());
    }

    /**
     * Used to get the proxy server instance.
     *
     * @return The proxy server.
     */
    public static ProxyServer getServer() {
        return Leaf.server;
    }

    /**
     * Used to get the logger instance.
     *
     * @return The logger.
     */
    public static Logger getLogger() {
        return Leaf.logger;
    }
}
