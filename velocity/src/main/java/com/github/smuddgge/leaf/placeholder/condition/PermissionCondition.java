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
 * server_color:
 *   condition: "PERMISSION"
 *   options:
 *     Default: "&f"
 *     server.red: "&c"
 *     server.blue: "&b"
 * }
 */
public class PermissionCondition implements Condition {

    @Override
    public @NotNull String getConditionIdentifier() {
        return "PERMISSION";
    }

    @Override
    public @Nullable String getValue(@NotNull ConfigurationSection section, @Nullable User user, @NotNull String identifier) {

        if (user == null) return null;

        final String condition = section.getString("condition");

        if (condition == null) {
            Leaf.get().getLogger().warn("&eThe placeholder &f" + identifier + "&e does not have a condition field. " +
                    "Please check your placeholders. " +
                    "To see how placeholders should be formatted see the wiki.");
            return null;
        }

        if (condition.split(":").length != 1) {
            Leaf.get().getLogger().warn("&eIncorrect permission placeholder &f" + identifier + "&e. " +
                    "The condition should just be \"PERMISSION\". " +
                    "The options are the actual permissions to check.");
            return null;
        }

        List<String> options = section.getKeys("options");

        if (options.isEmpty()) {
            Leaf.get().getLogger().warn("&eIncorrect permission placeholder &f" + identifier + ". " +
                    "Does not contain a options field. " +
                    "Please see the wiki for how placeholders should be configured.");
            return null;
        }

        for (final String key : options) {
            if (!user.hasPermission(key)) continue;
            return section.getSection("options").getString(key);
        }

        return section.getSection("options").getString("default", null);
    }
}
