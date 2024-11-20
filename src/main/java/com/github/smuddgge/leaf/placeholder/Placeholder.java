package com.github.smuddgge.leaf.placeholder;

import com.github.smuddgge.leaf.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
     * {@literal <player>} would be "player_name" or "player" or "name".
     *
     * @return The placeholder's identifiers.
     */
    @NotNull List<String> getIdentifierList();

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
     * Get the placeholder identifiers as formatted strings.
     * <p>
     * For example: {@literal <player_name>}.
     * @return The list of formatted identifiers.
     */
    default @NotNull List<String> getFormattedIdentifierList() {
        return this.getIdentifierList().stream()
                .map(identifier -> this.getType().getPrefix() + identifier + this.getType().getSuffix())
                .toList();
    }

    default @NotNull String asString() {
        return String.join(" ", this.getFormattedIdentifierList());
    }

    /**
     * Checks if the string contains the identifier or any of the aliases.
     *
     * @param string The string to check.
     * @return True if teh string contains one of the identifiers.
     */
    default boolean isIn(@NotNull String string) {
        for (String formattedIdentifier : this.getFormattedIdentifierList()) {
            if (string.contains(formattedIdentifier)) return true;
        }
        return false;
    }

    /**
     * If a identifier from this placeholder is in another placeholder.
     *
     * @param placeholder The placeholder to cross-reference.
     * @return True if there is a matching identifier in both.
     */
    default boolean overlaps(@NotNull Placeholder placeholder) {
        for (String identifier : this.getIdentifierList()) {
            for (String otherIdentifier : placeholder.getIdentifierList()) {
                if (identifier.equals(otherIdentifier)) return true;
            }
        }
        return false;
    }
}
