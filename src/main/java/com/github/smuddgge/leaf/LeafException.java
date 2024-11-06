package com.github.smuddgge.leaf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeafException extends RuntimeException {

    public LeafException(@Nullable Exception exception, @NotNull final String methodName, @NotNull final String cause) {
        super(cause, exception);
    }

    public LeafException(@NotNull final String methodName, @NotNull final String cause) {
        super(cause);
    }
}
