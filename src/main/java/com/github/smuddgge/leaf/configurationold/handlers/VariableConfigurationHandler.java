package com.github.smuddgge.leaf.configurationold.handlers;

import com.github.smuddgge.leaf.configurationold.ConfigurationHandler;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.placeholders.VariablePlaceholder;
import com.github.smuddgge.squishyconfiguration.implementation.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the variable configuration files.
 */
public class VariableConfigurationHandler extends ConfigurationHandler {

    private List<String> activeVariablePlaceholderList = new ArrayList<>();

    /**
     * Used to create a variable configuration handler.
     *
     * @param pluginFolder The instance of the plugin folder.
     */
    public VariableConfigurationHandler(File pluginFolder) {
        super(pluginFolder, "variables");
    }

    @Override
    public YamlConfiguration createDefaultConfiguration(File directory) {
        return new YamlConfiguration(directory, "variables.yml");
    }

    @Override
    public void reload() {
        for (String identifier : this.activeVariablePlaceholderList) {
            PlaceholderManager.unregister(identifier);
        }

        this.activeVariablePlaceholderList = new ArrayList<>();

        for (YamlConfiguration configuration : this.configFileList) {
            for (String identifier : configuration.getKeys()) {
                PlaceholderManager.register(
                        new VariablePlaceholder(identifier, configuration.getSection(identifier))
                );
                this.activeVariablePlaceholderList.add(identifier);
            }
        }
    }
}
