package com.github.smuddgge.leaf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeafException extends RuntimeException {

    public LeafException(@Nullable Exception exception, @NotNull final String methodName, @NotNull final String cause, @NotNull final String helpMessage) {
        super(cause, exception);
    }

    public LeafException(@NotNull final String methodName, @NotNull final String cause, @NotNull final String helpMessage) {
        this(null, methodName, cause, helpMessage);
    }

    public LeafException(@Nullable Exception exception, @NotNull final String methodName, @NotNull final String cause) {
        this(exception, methodName, cause, "This is a unexpected error, please report it to the developer.");
    }

    public LeafException(@NotNull final String methodName, @NotNull final String cause) {
        this(null, methodName, cause, "This is a unexpected error, please report it to the developer.");
    }
}
