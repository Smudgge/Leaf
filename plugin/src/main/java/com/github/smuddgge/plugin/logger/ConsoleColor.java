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

import org.jetbrains.annotations.NotNull;

/**
 * Represents the possible color codes you
 * can use in {@link Logger}'s.
 *
 * <li>
 * Console color codes are found here:
 * <a href="https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println">
 * Stackoverflow Java Console Colors
 * </a>
 * </li>
 */
public enum ConsoleColor {
    RESET("\033[0m", "&r"),
    WHITE("\033[0m", "&f"),
    GRAY("\033[0;37m", "&7"),
    RED("\033[0;31m", "&c"),
    YELLOW("\033[0;33m", "&e"),
    GREEN("\033[0;32m", "&a"),
    BLUE("\033[0;34m", "&b"),
    CYAN("\033[0;36m", "&3"),
    PURPLE("\033[0;35m", "&5");

    private final @NotNull String code;
    private final @NotNull String pattern;

    /**
     * Used to create a console color.
     *
     * @param code    The instance of the color code to replace with.
     * @param pattern The pattern that will be replaced with the color code.
     */
    ConsoleColor(@NotNull String code, @NotNull String pattern) {
        this.code = code;
        this.pattern = pattern;
    }

    public @NotNull String getCode() {
        return this.code;
    }

    public @NotNull String getPattern() {
        return this.pattern;
    }

    @Override
    public String toString() {
        return this.getPattern();
    }

    /**
     * Used to parse the colors in a string.
     * Converts the {@link ConsoleColor#pattern}
     * to the java color code.
     *
     * @param string The instance of a string.
     * @return The parsed string.
     */
    public static @NotNull String parse(@NotNull String string) {

        // Loop though all the available colors.
        for (ConsoleColor color : ConsoleColor.values()) {
            string = string.replace(color.getPattern(), color.getCode());
        }

        return string;
    }
}
