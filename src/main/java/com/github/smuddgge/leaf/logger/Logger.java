package com.github.smuddgge.leaf.logger;

import com.github.squishylib.common.indicator.Replicable;
import com.github.squishylib.common.logger.ConsoleColor;
import com.github.squishylib.common.logger.Level;
import com.mysql.cj.log.Log;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Handler;

public class Logger implements Replicable<Logger> {

    private final ComponentLogger componentLogger;
    private final com.github.squishylib.common.logger.Logger logger;

    public Logger(@NotNull ComponentLogger componentLogger, @Nullable String prefix) {
        this.componentLogger = componentLogger;

        // Please note that the logger com.github.smuddgge.leaf.logger will not be used!
        // This class will just use he component logger provided to log.
        this.logger = new com.github.squishylib.common.logger.Logger("com.github.smuddgge.leaf");
        this.logger.setPrefix(prefix);
    }

    public Logger(@NotNull ComponentLogger componentLogger) {
        this(componentLogger, null);
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

    public @NotNull Logger error(@NotNull String message) {
        this.componentLogger.error("&c" + this.getPrefixFormatted() + message + "&r");
        return this;
    }

    public @NotNull Logger warn(@NotNull String message) {
        this.componentLogger.warn("&e" + this.getPrefixFormatted() + message+ "&r");
        return this;
    }

    public @NotNull Logger info(@NotNull String message) {
        this.componentLogger.info("&7" + this.getPrefixFormatted() + message+ "&r");
        return this;
    }

    public @NotNull Logger debug(@NotNull String message) {
        this.componentLogger.debug("&7" + this.getPrefixFormatted() + message+ "&r");
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
