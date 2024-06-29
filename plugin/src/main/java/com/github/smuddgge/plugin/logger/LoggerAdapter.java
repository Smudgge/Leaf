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

package com.github.smuddgge.plugin.logger;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Used to create a {@link Logger} using an
 * existing {@link java.util.logging.Logger}.
 */
public class LoggerAdapter extends Logger {

    private final @NotNull ComponentLogger logger;

    /**
     * Used to create a new instance of a logger.
     *
     * @param debugMode Weather or not the logger is in debug mode.
     */
    public LoggerAdapter(boolean debugMode, @NotNull ComponentLogger logger) {
        super(debugMode);
        this.logger = logger;
    }

    @Override
    public @NotNull Logger log(@NotNull String message) {
        this.logger.info(ConsoleColor.parse("&7" + this.getMessageAsLog(message)));
        return this;
    }

    @Override
    public @NotNull Logger debug(@NotNull String message) {
        if (!this.getDebugMode()) return this;
        this.logger.debug(ConsoleColor.parse(this.getMessageAsLog("&7[Debug] &7" + message)));
        return this;
    }

    @Override
    public @NotNull Logger warn(@NotNull String message) {
        this.logger.warn(ConsoleColor.parse("&7" + this.getMessageAsWarn(message)));
        return this;
    }

    @Override
    public @NotNull Logger duplicate() {
        return new LoggerAdapter(this.getDebugMode(), this.logger)
                .setLogPrefix(this.getLogPrefix())
                .setWarnPrefix(this.getWarnPrefix());
    }
}
