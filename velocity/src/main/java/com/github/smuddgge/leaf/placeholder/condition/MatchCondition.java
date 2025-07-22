package com.github.smuddgge.leaf.placeholder.condition;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.placeholder.Condition;
import com.github.smuddgge.leaf.user.User;
import com.github.squishylib.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    public @NotNull String getConditionIdentifier() {
        return "MATCH";
    }

    @Override
    public @Nullable String getValue(@NotNull ConfigurationSection section, @Nullable User user, @NotNull String identifier) {
        final String condition = section.getString("condition");

        if (condition == null) {
            Leaf.get().getLogger().warn("&eThe placeholder &f" + identifier + "&e does not have a condition field. " +
                    "Please check your placeholders. " +
                    "To see how placeholders should be formatted see the wiki.");
            return null;
        }

        if (condition.split(":").length != 2) {
            Leaf.get().getLogger().warn("&eIncorrect match placeholder &f" + identifier + "&e. " +
                    "The condition should be formated like MATCH:string. " +
                    "Where the string can contain leaf placeholders with <> and/or {}.");
            return null;
        }

        final String string = condition.split(":")[1];
        final String pattern = Leaf.get().getPlaceholderManager().parseLeafPlaceholders(string, user);

        List<String> options = section.getKeys("options");

        if (options.isEmpty()) {
            Leaf.get().getLogger().warn("&eIncorrect match placeholder &f" + identifier + ". " +
                    "Does not contain a options field. " +
                    "Please see the wiki for how placeholders should be configured.");
            return null;
        }

        for (final String key : options) {
            if (!key.equals(pattern)) continue;
            return section.getSection("options").getString(key);
        }

        return section.getSection("options").getString("default", null);
    }
}
