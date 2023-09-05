package com.github.smuddgge.leaf.placeholders;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;

/**
 * Represents a custom placeholder.
 */
public class CustomConditionalPlaceholder implements Placeholder {

    private final String identifier;

    private final ConfigurationSection section;

    private PlaceholderCondition condition;

    private boolean valid = true;

    /**
     * Used to create a custom placeholder.
     *
     * @param identifier The placeholder's identifier in the configuration.
     */
    public CustomConditionalPlaceholder(String identifier, ConfigurationSection section) {
        this.identifier = identifier;
        this.section = section;

        String configCondition = this.section.getString("condition");

        // If it has been incorrectly configured
        if (configCondition == null) {
            this.setInvalid();
            return;
        }

        // The placeholder's condition instance
        this.condition = ConditionManager.getCondition(this.section.getString("condition").split(":")[0]);

        // If it has been incorrectly configured
        if (this.condition == null) {
            this.setInvalid();
        }
    }

    /**
     * Used unregister the placeholder and post a console warning.
     */
    private void setInvalid() {
        MessageManager.warn("Unregistering invalid custom placeholder : " + identifier);
        this.valid = false;
    }

    @Override
    public PlaceholderType getType() {
        return PlaceholderType.CUSTOM;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getValue(User user) {
        if (!this.valid) return null;
        String value = this.condition.getValue(this.section, user);
        return value == null ? this.section.getSection("options").getString("Default") : value;
    }

    @Override
    public String getValue() {
        if (!this.valid) return null;
        String value = this.condition.getValue(this.section);
        return value == null ? this.section.getSection("options").getString("Default") : value;
    }
}