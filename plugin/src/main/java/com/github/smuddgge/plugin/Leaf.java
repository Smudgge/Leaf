/*
 * Leaf - Velocity utility plugin.
 * Copyright (C) 2024 Smuddgge
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.smuddgge.plugin;

import com.github.smuddgge.plugin.logger.Logger;
import com.github.smuddgge.plugin.logger.LoggerAdapter;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@Plugin(
        id = "leaf",
        name = "Leaf",
        version = "@version@",
        description = "A velocity utility plugin.",
        authors = "Smudge"
)
public class Leaf {

    private static Leaf instance;

    private final @NotNull ProxyServer proxy;
    private final @NotNull Path dataDirectory;
    private final @NotNull Metrics.Factory metricsFactory;
    private final @NotNull Logger logger;

    @Inject
    public Leaf(
            ProxyServer proxy,
            @DataDirectory final Path dataDirectory,
            Metrics.Factory metricsFactory,
            ComponentLogger logger) {

        Leaf.instance = this;

        this.proxy = proxy;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
        this.logger = new LoggerAdapter(false, logger);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        // Set up b stats.
        this.metricsFactory.make(this, 17381);
    }

    public static Leaf getInstance() {
        return instance;
    }
}
