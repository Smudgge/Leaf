package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeafException extends RuntimeException {

    public static long lastErrorTimeStamp;

    /**
     *
     * @param exception The optional instance of the exception.
     * @param source For example: Leaf.get()
     * @param reason If there is a specific reason.
     * @param helpMessage A way of solving the problem.
     */
    public LeafException(@Nullable Exception exception, @NotNull final String source, @Nullable final String reason, @Nullable final String... helpMessage) {

        // Stop lots of errors.
        // Solve the first one first.
        if (lastErrorTimeStamp != -1 && System.currentTimeMillis() - lastErrorTimeStamp < 1000) {
            return;
        }

        lastErrorTimeStamp = System.currentTimeMillis();

        Logger logger = Leaf.get().getLogger();
        logger.error("source: &f" + source);
        if (reason != null) logger.error("reason: &f" + reason);
        if (helpMessage != null) logger.error("&7" + String.join("\n&7", helpMessage));
        logger.error("&c");

        for (StackTraceElement element : exception.getStackTrace()) {
            logger.error("[Trace] " + element.getMethodName() + ":" + element.getLineNumber());
        }
    }
}
