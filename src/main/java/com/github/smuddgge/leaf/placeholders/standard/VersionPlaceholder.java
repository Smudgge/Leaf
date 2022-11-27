package com.github.smuddgge.leaf.placeholders.standard;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.StandardPlaceholder;
import com.velocitypowered.api.plugin.Plugin;

/**
 * Represents the version placeholder.
 * Returns the version of this plugin.
 */
public class VersionPlaceholder extends StandardPlaceholder {

    @Override
    public String getIdentifier() {
        return "version";
    }

    @Override
    public String getValue(User user) {
        return Leaf.class.getAnnotation(Plugin.class).version();
    }

    @Override
    public String getValue() {
        return this.getValue(null);
    }
}
