package com.github.smuddgge.leaf.placeholders.conditions;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderCondition;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.placeholders.PlaceholderType;

import java.util.List;
import java.util.Objects;

/**
 * Represents the match condition.
 */
public class MatchCondition implements PlaceholderCondition {

    @Override
    public String getIdentifier() {
        return "MATCH";
    }

    @Override
    public String getValue(ConfigurationSection section, User user) {
        String condition = section.getString("condition");
        String pattern = PlaceholderManager.parse(condition.split(":")[1], PlaceholderType.STANDARD, user);

        List<String> options = section.getKeys("options");

        if (options == null) {
            MessageManager.warn("Cannot find options in custom placeholder : " + condition);
            return null;
        }

        for (String key : options) {
            if (!Objects.equals(key, pattern)) continue;

            return section.getSection("options").getString(key);
        }
        return null;
    }

    @Override
    public String getValue(ConfigurationSection section) {
        return this.getValue(section, null);
    }
}
