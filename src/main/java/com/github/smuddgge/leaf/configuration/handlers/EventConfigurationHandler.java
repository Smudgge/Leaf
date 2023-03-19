package com.github.smuddgge.leaf.configuration.handlers;

import com.github.smuddgge.leaf.configuration.ConfigurationHandler;
import com.github.smuddgge.leaf.configuration.squishyyaml.YamlConfiguration;
import com.github.smuddgge.leaf.events.CustomEvent;
import com.github.smuddgge.leaf.events.Event;
import com.github.smuddgge.leaf.events.EventManager;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EventConfigurationHandler extends ConfigurationHandler {

    private List<CustomEvent> loadedEventList = new ArrayList<>();

    /**
     * Used to create an event's configuration handler.
     *
     * @param pluginFolder The instance of the plugin folder.
     */
    public EventConfigurationHandler(File pluginFolder) {
        super(pluginFolder, "events");
    }

    @Override
    public YamlConfiguration createDefaultConfiguration(File directory) {
        return new YamlConfiguration(directory, "events.yml");
    }

    @Override
    public void reload() {
        for (CustomEvent customEvent : this.loadedEventList) {
            EventManager.unRegister(customEvent);
        }

        this.loadedEventList = new ArrayList<>();
        this.configFileList = new ArrayList<>();
        this.registerFiles();

        for (CustomEvent customEvent : this.getCustomEvents()) {
            EventManager.register(customEvent);
            this.loadedEventList.add(customEvent);
        }

    }

    /**
     * Used to get the list of custom events from the configuration files.
     *
     * @return The list of custom events.
     */
    public List<CustomEvent> getCustomEvents() {
        List<CustomEvent> customEventList = new ArrayList<>();

        for (YamlConfiguration configuration : this.configFileList) {
            for (String identifier : configuration.getKeys()) {
                CustomEvent customEvent = new CustomEvent(identifier, configuration.getSection(identifier));
                customEventList.add(customEvent);
            }
        }

        return customEventList;
    }
}
