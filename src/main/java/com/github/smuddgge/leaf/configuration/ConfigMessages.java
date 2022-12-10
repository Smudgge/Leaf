package com.github.smuddgge.leaf.configuration;

import com.github.smuddgge.leaf.configuration.squishyyaml.YamlConfiguration;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.CustomConditionalPlaceholder;
import com.github.smuddgge.leaf.placeholders.Placeholder;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.placeholders.PlaceholderType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the messages configuration file.
 */
public class ConfigMessages extends YamlConfiguration {

    private static final List<String> registeredPlaceholders = new ArrayList<>();

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

        for (String placeholderIdentifier : ConfigMessages.registeredPlaceholders) {
            PlaceholderManager.unregister(placeholderIdentifier);
        }

        for (String placeholderIdentifier : ConfigMessages.get().getKeys("placeholders")) {
            ConfigMessages.registeredPlaceholders.add(placeholderIdentifier);

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

                continue;
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

    /**
     * Used to get the incorrect argument message.
     *
     * @param commandSyntax The command's syntax.
     * @return The incorrect argument message.
     */
    public static String getIncorrectArguments(String commandSyntax) {
        return ConfigMessages.config.getSection("messages")
                .getString("incorrect_arguments", "{error} Incorrect arguments. %command%")
                .replace("%command%", commandSyntax);
    }

    /**
     * Used to get the database error message.
     *
     * @return The database error message.
     */
    public static String getDatabaseDisabled() {
        return ConfigMessages.config.getSection("messages")
                .getString("database_disabled", "{error_colour}Database Disabled.");
    }

    /**
     * Used to get the database empty message.
     *
     * @return The database empty message.
     */
    public static String getDatabaseEmpty() {
        return ConfigMessages.config.getSection("messages")
                .getString("database_empty", "{error_colour}There are no records in the database.");
    }
}
