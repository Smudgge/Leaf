package com.github.smuddgge.leaf.placeholders.conditions;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderCondition;
import com.github.smuddgge.squishyyaml.YamlConfigurationSection;

public class MatchCondition implements PlaceholderCondition {

    @Override
    public String getIdentifier() {
        return "MATCH";
    }

    @Override
    public String getValue(YamlConfigurationSection section, User user) {
        return null;
    }

    @Override
    public String getValue(YamlConfigurationSection section) {
        return null;
    }
}
