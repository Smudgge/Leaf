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

package com.github.smuddgge.plugin.indicator;

import org.jetbrains.annotations.NotNull;

/**
 * Indicates if a class can be duplicated.
 *
 * @param <T> The final class where this interface is implemented.
 *            This will be the object returned when duplicated.
 */
public interface Replicable<T> {

    /**
     * Used to duplicate the class instance
     * which this is implemented in.
     *
     * @return The instance of the duplicated class.
     */
    @NotNull
    T duplicate();
}
