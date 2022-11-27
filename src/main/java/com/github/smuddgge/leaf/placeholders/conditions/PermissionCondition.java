package com.github.smuddgge.leaf.placeholders.conditions;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderCondition;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.placeholders.PlaceholderType;

import java.util.List;

/**
 * Represents the permission condition.
 */
public class PermissionCondition implements PlaceholderCondition {

    @Override
    public String getIdentifier() {
        return "PERMISSION";
    }

    @Override
    public String getValue(ConfigurationSection section, User user) {
        if (user == null) return null;

        String condition = section.getString("condition");
        String pattern = PlaceholderManager.parse(condition.split(":")[1], PlaceholderType.STANDARD, user);

        List<String> options = section.getKeys("options");

        if (options == null) {
            MessageManager.warn("Cannot find options in custom placeholder : " + condition);
            return null;
        }

        for (String key : options) {
            String permission = pattern.replace("?", key);

            if (!user.hasPermission(permission)) continue;

            return section.getSection("options").getString(key);
        }

        return null;
    }

    @Override
    public String getValue(ConfigurationSection section) {
        return null;
    }
}
