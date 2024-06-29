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
 * Indicates if a record can be converted into a class.
 *
 * @param <T> The instance of the class that the record
 *            is being converted into.
 */
public interface RecordConvertable<T extends Savable> {

    /**
     * Used to convert the record into the class.
     *
     * @return The instance of the class.
     */
    @NotNull
    T convert();
}
