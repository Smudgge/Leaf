package com.github.smuddgge.leaf.logger;

import com.github.squishylib.common.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SquishyLoggerAdapter extends Logger {

    private final @NotNull com.github.smuddgge.leaf.logger.Logger logger;

    public SquishyLoggerAdapter(@NotNull final com.github.smuddgge.leaf.logger.Logger logger) {
        // Note that this logger with the leaf package will never be used.
        super("com.github.smuddgge.leaf");

        this.logger = logger;
    }

    @Override
    public @Nullable String getPrefix() {
        return this.logger.getPrefix();
    }

    /**
     * If a prefix exists, it will at a space at the end.
     * Otherwise, it will return an empty string.
     *
     * @return The formatted prefix.
     */
    @Override
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
    @Override
    public @NotNull Logger setPrefix(@Nullable String prefix) {
        this.logger.setPrefix(prefix);
        return this;
    }

    @Override
    public @NotNull Logger error(@NotNull String message) {
        this.logger.error(message);
        return this;
    }

    @Override
    public @NotNull Logger warn(@NotNull String message) {
        this.logger.warn(message);
        return this;
    }

    @Override
    public @NotNull Logger info(@NotNull String message) {
        this.logger.info(message);
        return this;
    }

    @Override
    public @NotNull Logger debug(@NotNull String message) {
        this.logger.debug(message);
        return this;
    }

    /**
     * If you wish to have a space between the prefix before it,
     * you should include within the extension.
     *
     * @param prefixExtension The prefix to add to the current prefix.
     * @return A new logger with the extended prefix but linked log level.
     */
    @Override
    public @NotNull Logger extend(@NotNull String prefixExtension) {
        return new SquishyLoggerAdapter(this.logger.extend(prefixExtension));
    }

    @Override
    public @NotNull Logger duplicate() {
        return new SquishyLoggerAdapter(this.logger.duplicate());
    }
}
