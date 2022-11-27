package com.github.smuddgge.leaf.placeholders;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyyaml.ConfigurationSection;

/**
 * Represents a custom placeholder.
 */
public class CustomConditionalPlaceholder implements Placeholder {

    private final String identifier;

    private final ConfigurationSection section;

    private PlaceholderCondition condition;

    private boolean valid;

    /**
     * Used to create a custom placeholder.
     *
     * @param identifier The placeholder's identifier in the configuration.
     */
    public CustomConditionalPlaceholder(String identifier) {
        this.identifier = identifier;

        // The placeholder's configuration section
        this.section = ConfigMessages.get().getSection("placeholders").getSection(identifier);

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
        return this.condition.getValue(this.section, user);
    }

    @Override
    public String getValue() {
        if (!this.valid) return null;
        return this.condition.getValue(this.section);
    }
}