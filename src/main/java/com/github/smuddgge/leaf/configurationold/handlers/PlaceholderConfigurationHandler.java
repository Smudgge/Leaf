package com.github.smuddgge.leaf.configurationold.handlers;

import com.github.smuddgge.leaf.configurationold.ConfigurationHandler;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.CustomConditionalPlaceholder;
import com.github.smuddgge.leaf.placeholders.Placeholder;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.placeholders.PlaceholderType;
import com.github.smuddgge.squishyconfiguration.implementation.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the placeholder configuration handler.
 */
public class PlaceholderConfigurationHandler extends ConfigurationHandler {

    private List<String> customPlaceholderNameList = new ArrayList<>();

    /**
     * Used to create a placeholder configuration handler.
     *
     * @param pluginFolder The plugin's folder.
     */
    public PlaceholderConfigurationHandler(File pluginFolder) {
        super(pluginFolder, "placeholders");
    }

    @Override
    public YamlConfiguration createDefaultConfiguration(File directory) {
        return new YamlConfiguration(directory, "placeholders.yml");
    }

    @Override
    public void reload() {
        for (String identifier : this.customPlaceholderNameList) {
            PlaceholderManager.unregister(identifier);
        }

        this.customPlaceholderNameList = new ArrayList<>();
        this.configFileList = new ArrayList<>();
        this.registerFiles();

        for (Placeholder placeholder : this.getPlaceholders()) {
            PlaceholderManager.register(placeholder);
            this.customPlaceholderNameList.add(placeholder.getIdentifier());
        }
    }

    /**
     * Used to get the list of placeholders.
     *
     * @return The list of placeholders.
     */
    public List<Placeholder> getPlaceholders() {
        List<Placeholder> placeholderList = new ArrayList<>();

        for (YamlConfiguration configuration : this.configFileList) {
            for (String identifier : configuration.getKeys()) {

                // Check if it's a simple custom placeholder.
                String value = configuration.getString(identifier);

                if (value != null) {
                    placeholderList.add(
                            new Placeholder() {
                                @Override
                                public PlaceholderType getType() {
                                    return PlaceholderType.CUSTOM;
                                }

                                @Override
                                public String getIdentifier() {
                                    return identifier;
                                }

                                @Override
                                public String getValue(User user) {
                                    return PlaceholderManager.parse(value, PlaceholderType.STANDARD, user);
                                }

                                @Override
                                public String getValue() {
                                    return PlaceholderManager.parse(value, PlaceholderType.STANDARD);
                                }
                            }
                    );

                    continue;
                }

                // Register conditional custom placeholder.
                placeholderList.add(
                        new CustomConditionalPlaceholder(identifier, configuration.getSection(identifier))
                );
            }
        }

        return placeholderList;
    }
}
