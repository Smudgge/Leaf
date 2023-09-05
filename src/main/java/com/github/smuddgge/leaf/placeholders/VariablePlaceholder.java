package com.github.smuddgge.leaf.placeholders;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Represents a variable placeholder.
 */
public class VariablePlaceholder implements Placeholder {

    private final String identifier;
    private final ConfigurationSection section;

    public VariablePlaceholder(String identifier, ConfigurationSection section) {
        this.identifier = identifier;
        this.section = section;
    }

    @Override
    public PlaceholderType getType() {
        return PlaceholderType.STANDARD;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getValue(User user) {
        if (Leaf.isDatabaseDisabled()) return this.getValue();
        if (user == null) return this.getValue();

        PlayerRecord playerRecord = user.getRecord();

        try {

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> variables = gson.fromJson(playerRecord.variables, type);

            if (variables.containsKey(this.identifier)) return variables.get(this.identifier).toString();
        } catch (Exception exception) {
            return this.getValue();
        }

        return this.getValue();
    }

    @Override
    public String getValue() {
        return this.section.get("default", "null").toString();
    }
}
