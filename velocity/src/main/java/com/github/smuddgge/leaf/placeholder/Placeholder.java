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
         * The prefix and suffix go ether side of the placeholder name.
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
     * For example the name of the placeholder
     * {@literal <player>} would be "player_name" or "player" or "name".
     *
     * @return The placeholder's names.
     */
    @NotNull List<String> getNameList();

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
    @Nullable String getValue(@Nullable User user, @NotNull String string);

    /**
     * Get the placeholder names as formatted strings.
     * <p>
     * For example: {@literal <player_name>}.
     *
     * @return The list of formatted names.
     */
    default @NotNull List<String> getFormattedNameList() {
        return this.getNameList().stream()
                .map(identifier -> this.getType().getPrefix() + identifier + this.getType().getSuffix())
                .toList();
    }

    default @NotNull String asString() {
        return String.join(" ", this.getFormattedNameList());
    }

    /**
     * Checks if the string contains this placeholder.
     *
     * @param string The string to check.
     * @return True if the string contains one of the names.
     */
    default boolean isIn(@NotNull String string) {
        for (String name : this.getNameList()) {
            if (string.contains(this.getType().getPrefix() + name)) return true;
        }
        return false;
    }

    /**
     * If a name from this placeholder is in another placeholder.
     *
     * @param placeholder The placeholder to cross-reference.
     * @return True if there is a matching name in both.
     */
    default boolean overlaps(@NotNull Placeholder placeholder) {
        for (String identifier : this.getNameList()) {
            for (String otherIdentifier : placeholder.getNameList()) {
                if (identifier.equals(otherIdentifier)) return true;
            }
        }
        return false;
    }

    /**
     * Used to convert these placeholder names within the string.
     *
     * @param string The string to parse.
     * @param user   The user context.
     * @return The parsed string.
     */
    default @NotNull String parse(@NotNull String string, @Nullable User user) {

        // Loop though the placeholders names.
        for (String name : this.getNameList()) {

            // Attempt to get the index of the first occurrence in the string.
            final int index = string.indexOf(this.getType().getPrefix() + name);
            if (index == -1) continue;

            // Get the placeholder by its self.
            final String choppedLeft = string.substring(index);
            final int endIndex = this.getEndIndex(choppedLeft);
            final String placeholder = choppedLeft.substring(0, endIndex + 1);

            // Get the result of this placeholder.
            final String result = this.getValue(user, placeholder);

            string = string.replace(placeholder, result == null ? "null" : result);
        }

        // Check if this placeholder still exists in the string.
        if (this.isIn(string)) return this.parse(string, user);
        return string;
    }

    default int getEndIndex(@NotNull String choppedLeft) {
        int bracketDepth = 0;
        int index = 0;

        for (char c : choppedLeft.toCharArray()) {
            if (c == this.getType().getPrefix().toCharArray()[0]) bracketDepth++;

            if (c == this.getType().getSuffix().toCharArray()[0]) {
                if (bracketDepth <= 1) {
                    return index;
                }
                bracketDepth--;
            }

            index++;
        }
        return choppedLeft.length() - 1;
    }
}
