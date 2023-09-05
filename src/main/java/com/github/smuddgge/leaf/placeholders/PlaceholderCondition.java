package com.github.smuddgge.leaf.placeholders;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;

/**
 * Represents a placeholder's condition.
 */
public interface PlaceholderCondition {

    /**
     * Used to get the condition's identifier.
     * The identifier is the uppercase word before the colon.
     *
     * @return The condition's identifier.
     */
    String getIdentifier();

    /**
     * Used to get the value of the custom placeholder with context of a user.
     *
     * @param section The placeholder's configuration section.
     * @param user    The instance of the user.
     * @return The value.
     */
    String getValue(ConfigurationSection section, User user);

    /**
     * Used to get the value of the custom placeholder.
     *
     * @param section The placeholder's configuration section.
     * @return The value.
     */
    String getValue(ConfigurationSection section);
}
