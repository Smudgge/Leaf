package com.github.smuddgge.leaf.placeholder;

import com.github.smuddgge.leaf.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A string that gets replaced with a value.
 * <p>
 * There are two main types: standard and custom.
 * <p>
 * Standard placeholders are hard coded into the plugin,
 * for example {@literal <player>} which returns the players name.
 * <p>
 * Custom placeholders are made by the users of leaf.
 * Often its adding formatting to standard placeholders.
 * <h1>Usage</h1>
 * <pre>
 * - Create a new class in the standard folder that ends with "Placeholder".
 * - Implement this interface.
 * - Register the placeholder with the manager.
 */
public interface Placeholder {

    /**
     * The type of placeholder.
     * <p>
     * There are two main types: standard and custom.
     * <p>
     * Standard placeholders are hard coded into the plugin,
     * for example {@literal <player>} which returns the players name.
     * <p>
     * Custom placeholders are made by the users of leaf.
     * Often its adding formatting to standard placeholders.
     */
    enum Type {
        STANDARD("<", ">"),
        CUSTOM("{", "}");

        private final String prefix;
        private final String suffix;

        /**
         * Used to create a new placeholder type.
         * The prefix and suffix go ether side of the placeholder identifier.
         *
         * @param prefix The prefix of the placeholder.
         * @param suffix The suffix of the placeholder.
         */
        Type(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public String getSuffix() {
            return this.suffix;
        }
    }

    /**
     * For example the identifier of the placeholder
     * {@literal <player>} would be "player".
     *
     * @return The placeholder's identifier.
     */
    @NotNull String getIdentifier();

    /**
     * The other names this placeholder will convert for.
     * For example the alias of the {@literal <player>}
     * placeholder could be {@literal <playername>}.
     *
     * @return The placeholder's aliases.
     */
    @NotNull List<String> getAliases();

    /**
     * If the placeholder is ether hard coded (standard) or if
     * it was made by the user (custom).
     *
     * @return The placeholder's type.
     */
    @NotNull Type getType();

    /**
     * Get the current value of the placeholder.
     *
     * @param user Optional argument of a user to get context of.
     * @return The current value or null if no value.
     */
    @Nullable String getValue(@Nullable User user);

    /**
     * Returns the identifier wrapped in the prefix and suffix.
     * <p>
     * For example, for the player placeholder this would
     * return "{@literal <player>}".
     *
     * @return The formatted identifier.
     */
    default @NotNull String getFormatted() {
        return this.getType().getPrefix() + this.getIdentifier() + this.getType().getSuffix();
    }
}
