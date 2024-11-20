package com.github.smuddgge.leaf.placeholder;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.logger.Logger;
import com.github.smuddgge.leaf.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Acts as the entry point for leaf placeholders.
 */
public class PlaceholderManager {

    private final List<Placeholder> placeholderList;

    public PlaceholderManager() {
        this.placeholderList = new ArrayList<>();
    }

    public @NotNull List<Placeholder> getPlaceholders() {
        return this.placeholderList;
    }

    public @NotNull PlaceholderManager register(@NotNull final Placeholder placeholder) {
        Logger logger = Leaf.get().getLogger().extend(" &7[Placeholders");

        // Has the placeholder already been registered?
        if (this.contains(placeholder)) {
            logger.info("&eAttempted to register the placeholder &f%placeholder% &ebut it already exists! Make sure there are no identifier or alias duplicates."
                    .replace("%placeholder%", placeholder.asString())
            );
            return this;
        }

        this.placeholderList.add(placeholder);
        logger.info("&aRegistered &7%placeholder%"
                .replace("%placeholder%", placeholder.asString())
        );
        return this;
    }

    public @NotNull PlaceholderManager unregister(@NotNull final Placeholder placeholder) {
        this.placeholderList.remove(placeholder);
        Logger logger = Leaf.get().getLogger().extend(" &7[Placeholders");
        logger.info("&cUnregistered &7%placeholder%"
                .replace("%placeholder%", placeholder.asString())
        );
        return this;
    }

    /**
     * Is the placeholder already registered?
     * <p>
     * Checks all identifiers.
     *
     * @param placeholder The placeholder to check.
     * @return True if it is already registered.
     */
    private boolean contains(@NotNull final Placeholder placeholder) {
        // For each placeholder
        for (Placeholder temp : this.placeholderList) {
            if (temp.overlaps(placeholder)) return true;
        }
        return false;
    }

    public @NotNull String parse(@NotNull String string, @Nullable User user) {
        for (Placeholder placeholder : this.placeholderList) {

            // Does the string not contain one of the placeholder's identifiers?
            if (!placeholder.isIn(string)) continue;



        }
    }
}
