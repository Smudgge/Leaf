package com.github.smuddgge.leaf.configuration;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.CustomConditionalPlaceholder;
import com.github.smuddgge.leaf.placeholders.Placeholder;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.placeholders.PlaceholderType;
import com.github.smuddgge.squishyyaml.YamlConfiguration;

import java.io.File;

public class ConfigMessages extends YamlConfiguration {

    private static ConfigMessages config;

    /**
     * Used to create an instance of the message's configuration file.
     *
     * @param folder The plugin's folder.
     */
    public ConfigMessages(File folder) {
        super(folder, "messages.yml");

        this.load();
    }

    /**
     * Used to initialise the message's configuration file instance.
     *
     * @param folder The plugin's folder.
     */
    public static void initialise(File folder) {
        ConfigMessages.config = new ConfigMessages(folder);
    }

    /**
     * Used to reload the message's configuration file instance.
     */
    public static void reload() {
        ConfigMessages.config.load();

        for (String placeholderIdentifier : ConfigMessages.get().getKeys("placeholders")) {
            PlaceholderManager.unregister(placeholderIdentifier);

            String value = ConfigMessages.get().getSection("placeholders").getString(placeholderIdentifier);

            if (value != null) {
                PlaceholderManager.register(
                        new Placeholder() {
                            @Override
                            public PlaceholderType getType() {
                                return PlaceholderType.CUSTOM;
                            }

                            @Override
                            public String getIdentifier() {
                                return placeholderIdentifier;
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
            }

            PlaceholderManager.register(new CustomConditionalPlaceholder(placeholderIdentifier));
        }
    }

    /**
     * Used to get the instance of the message's configuration file.
     *
     * @return The message's configuration file.
     */
    public static ConfigMessages get() {
        return ConfigMessages.config;
    }
}
