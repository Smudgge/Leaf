package com.github.smuddgge.leaf.logger;

import com.github.smuddgge.leaf.Leaf;
import com.github.squishylib.common.indicator.Replicable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class Logger implements Replicable<Logger> {

    private final ComponentLogger componentLogger;
    private final com.github.squishylib.common.logger.Logger logger;
    private boolean debugMode;

    public Logger(@NotNull ComponentLogger componentLogger, @Nullable String prefix) {
        this.componentLogger = componentLogger;

        // Please note that the logger com.github.smuddgge.leaf.logger will not be used!
        // This class will just use he component logger provided to log.
        this.logger = new com.github.squishylib.common.logger.Logger("com.github.smuddgge.leaf");
        this.logger.setPrefix(prefix);

        this.debugMode = false;
    }

    public Logger(@NotNull ComponentLogger componentLogger) {
        this(componentLogger, null);
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public @NotNull Logger setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    public @Nullable String getPrefix() {
        return this.logger.getPrefix();
    }

    /**
     * If a prefix exists, it will at a space at the end.
     * Otherwise, it will return an empty string.
     *
     * @return The formatted prefix.
     */
    public @NotNull String getPrefixFormatted() {
        return this.logger.getPrefixFormatted();
    }

    /**
     * This logger will add a space to the end of the prefix
     * to separate it from the message.
     *
     * @param prefix The prefix.
     * @return This instance.
     */
    public @NotNull Logger setPrefix(@Nullable String prefix) {
        this.logger.setPrefix(prefix);
        return this;
    }

    private void send(@NotNull String message, @NotNull String color, @NotNull Consumer<Component> runLog) {
        for (final String part : message.split("\n")) {
            runLog.accept(Leaf.get().getPlaceholderManager().parseColorsOnly(
                    color + this.getPrefixFormatted() + part
            ));
        }
    }

    public @NotNull Logger error(@NotNull String message) {
        this.send(message, "&c", this.componentLogger::error);
        return this;
    }

    public @NotNull Logger warn(@NotNull String message) {
        this.send(message, "&e", this.componentLogger::warn);
        return this;
    }

    public @NotNull Logger info(@NotNull String message) {
        this.send(message, "&7", this.componentLogger::info);
        return this;
    }

    public @NotNull Logger debug(@NotNull String message) {
        if (this.debugMode) {
            this.send(message, "&7", this.componentLogger::info);
        }
        return this;
    }

    /**
     * Separate levels defined in the config.yml
     * <p>
     * For example, if commands is set to true in config.yml
     * it will log when commands are registered and unregistered.
     */
    public enum Opt {
        B_STATS,
        COMMANDS
    }

    public @NotNull Logger optional(@NotNull Opt level, @NotNull String message) {
        if (Leaf.get().getConfig().getSection("logging")
                .getBoolean(level.name().toLowerCase())) {

            this.info(message);
        }
        return this;
    }

    /**
     * If you wish to have a space between the prefix before it,
     * you should include within the extension.
     *
     * @param prefixExtension The prefix to add to the current prefix.
     * @return A new logger with the extended prefix but linked log level.
     */
    public @NotNull Logger extend(@NotNull String prefixExtension) {
        if (this.getPrefix() == null) prefixExtension = prefixExtension.trim();
        else prefixExtension = this.getPrefix() + prefixExtension;
        return this.duplicate().setPrefix(prefixExtension);
    }

    @Override
    public @NotNull Logger duplicate() {
        return new Logger(this.componentLogger, this.logger.getPrefix());
    }
}
