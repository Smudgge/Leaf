/*
 * Kerb
 * Event and request distributor server software.
 *
 * Copyright (C) 2023  Smuddgge
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

package com.github.smuddgge.leaf.task;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a task container.
 * This can be implemented to run tasks in the class instance.
 */
public class TaskContainer {

    private static final int SLEEP_TIME_MILLIS = 100;

    private @NotNull Map<String, Task> taskMap;

    /**
     * Used to create a new task container.
     * Lets you run tasks on threads.
     */
    public TaskContainer() {
        this.taskMap = new HashMap<>();
    }

    /**
     * Run a task in the future.
     *
     * @param runnable   The instance of the task.
     * @param duration   The duration to wait.
     * @param identifier The identifier to set the task.
     * @return This instance.
     */
    protected @NotNull TaskContainer runTask(@NotNull Runnable runnable, @NotNull Duration duration, @NotNull String identifier) {

        // Check if the identifier already exists.
        if (this.taskMap.containsKey(identifier)) {
            throw new RuntimeException("Identifier already exists within task container.");
        }

        // Start the thread.
        new Thread(() -> {

            // Get the current time.
            long from = System.currentTimeMillis();

            // Set the running variable.
            AtomicBoolean running = new AtomicBoolean(true);

            // Create the instance of the task.
            Task task = () -> running.set(false);

            // Wait till duration has completed.
            while (running.get()) {
                try {

                    // Check if it's time to run the runnable.
                    if (System.currentTimeMillis() - from >= duration.toMillis()) break;

                    // Wait a few mills.
                    Thread.sleep(SLEEP_TIME_MILLIS);

                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
            }

            // Check if the task was canceled.
            if (!running.get()) return;

            // Run the task.
            runnable.run();

        }).start();

        return this;
    }

    /**
     * Used to stop a single task.
     *
     * @param identifier The instance of the task's identifier.
     * @return This instance.
     */
    public @NotNull TaskContainer stopTask(@NotNull String identifier) {
        Task task = this.taskMap.get(identifier);

        // Check if the task doesn't exist.
        if (task == null) return this;

        // Cancel the task.
        task.cancel();
        this.taskMap.remove(identifier);
        return this;
    }

    /**
     * Used to stop all tasks in the container.
     *
     * @return This instance.
     */
    public @NotNull TaskContainer stopAllTasks() {
        for (Task task : this.taskMap.values()) {
            task.cancel();
        }

        this.taskMap = new HashMap<>();
        return this;
    }
}
