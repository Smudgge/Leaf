package com.github.smuddgge.leaf.placeholder.condition;

import com.github.smuddgge.leaf.placeholder.Condition;
import com.github.smuddgge.leaf.user.User;
import com.github.squishylib.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <pre>
 * Example:
 * {@code
 * vanish_colour:
 *   condition: "MATCH:<vanished>"
 *   options:
 *     Default: "<#ffffee>"
 *     true: "<#c0fce6>"
 *     false: "<#ffffee>"
 * }
 */
public class MatchCondition implements Condition {

    @Override
    public @NotNull String getIdentifier() {
        return "MATCH";
    }

    @Override
    public @Nullable String getValue(@NotNull ConfigurationSection section, @Nullable User user) {
        final String condition = section.getString("condition");
        final String pattern =
        return "";
    }
}
