package com.github.smuddgge.leaf.exception;

import com.github.smuddgge.leaf.ConsoleColor;
import com.github.smuddgge.leaf.Leaf;
import com.velocitypowered.api.plugin.Plugin;

public class LeafException extends RuntimeException {

    public LeafException(final Exception exception, final String message) {
        super(ConsoleColor.parse("\n" +
                "&7--------------------------------------------------\n" +
                "&cLeaf Version: &r" + Leaf.class.getAnnotation(Plugin.class).version() + "\n" +
                "&cServer Version: &r" + Leaf.getServer().getVersion().getVersion() + "\n" +
                (message == null ? "\n" : "&7\n&c" + message + "\n&c")
        ), exception);
    }

    public LeafException(final Exception exception) {
        this(exception, exception.getMessage());
    }

    public LeafException(final String message) {
        this(null, message);
    }

    public LeafException() {
        this(null, null);
    }
}
