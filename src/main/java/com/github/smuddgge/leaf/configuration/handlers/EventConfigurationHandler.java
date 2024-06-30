package com.github.smuddgge.leaf.configuration.handlers;

import com.github.smuddgge.leaf.configuration.ConfigurationHandler;
import com.github.smuddgge.leaf.events.Event;
import com.github.smuddgge.leaf.events.EventManager;
import com.github.smuddgge.leaf.events.EventType;
import com.github.smuddgge.squishyconfiguration.implementation.YamlConfiguration;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.console.Console;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EventConfigurationHandler extends ConfigurationHandler {

    private List<Event> loadedEventList = new ArrayList<>();

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
        for (Event event : this.loadedEventList) {
            EventManager.unRegister(event);
        }

        this.loadedEventList = new ArrayList<>();
        this.configFileList = new ArrayList<>();
        this.registerFiles();

        for (Event event : this.getEvents()) {
            EventManager.register(event);
            this.loadedEventList.add(event);
        }
    }

    /**
     * Used to get the list of custom events from the configuration files.
     *
     * @return The list of custom events.
     */
    public List<Event> getEvents() {
        List<Event> eventList = new ArrayList<>();

        for (YamlConfiguration configuration : this.configFileList) {
            for (String identifier : configuration.getKeys()) {

                ConfigurationSection section = configuration.getSection(identifier);
                String typeString = section.getString("type", "none");
                EventType type = EventType.getFromType(typeString);

                if (type == null) {
                    Console.warn("Invalid event : &f" + identifier + ". &eType doesnt exists : &f" + typeString);
                    continue;
                }

                // Add event.
                eventList.add(type.create(identifier, section));
            }
        }

        return eventList;
    }
}
