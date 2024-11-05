package com.github.smuddgge.leaf.configurationold.handlers;

import com.github.smuddgge.leaf.commands.CommandAliases;
import com.github.smuddgge.leaf.configurationold.ConfigurationHandler;
import com.github.smuddgge.squishyconfiguration.implementation.YamlConfiguration;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishyconfiguration.memory.MemoryConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * <h1>Handles the commands configs</h1>
 */
public class CommandConfigurationHandler extends ConfigurationHandler {

    /**
     * Used to create a command's configuration handler.
     *
     * @param pluginFolder The instance of the plugin folder.
     */
    public CommandConfigurationHandler(File pluginFolder) {
        super(pluginFolder, "commands");
    }

    @Override
    public YamlConfiguration createDefaultConfiguration(File directory) {
        return new YamlConfiguration(directory, "commands.yml");
    }

    @Override
    public void reload() {
        this.configFileList = new ArrayList<>();
        this.registerFiles();
    }

    /**
     * Used to get all the command's configuration sections.
     *
     * @return The list of configurations.
     */
    public List<ConfigurationSection> getAll() {
        List<ConfigurationSection> list = new ArrayList<>();

        for (YamlConfiguration configuration : this.configFileList) {
            for (String identifier : configuration.getKeys()) {
                list.add(configuration.getSection(identifier));
            }
        }

        return list;
    }

    /**
     * Used to get all the command identifiers.
     *
     * @return List of command identifiers.
     */
    public List<String> getAllIdentifiers() {
        List<String> list = new ArrayList<>();

        for (YamlConfiguration configuration : this.configFileList) {
            list.addAll(configuration.getKeys());
        }

        return list;
    }

    /**
     * Used to get a command from the configuration files.
     * If the command does not exist, it will return an empty configuration section.
     *
     * @param identifier The identifier of the command.
     * @return The instance of the configuration section.
     */
    public ConfigurationSection getCommand(String identifier) {
        for (YamlConfiguration configuration : this.configFileList) {
            if (configuration.getKeys().contains(identifier)) {
                return configuration.getSection(identifier);
            }
        }

        return new MemoryConfigurationSection(new HashMap<>());
    }

    /**
     * Used to get the commands a type.
     *
     * @param identifier The command's identifier.
     * @return The commands type.
     */
    public String getCommandType(String identifier) {
        return this.getCommand(identifier).getString("type");
    }

    /**
     * Used to get the main command name given the configuration identifier.
     *
     * @param identifier The identifier of the command.
     * @return The name of the main command name.
     */
    public String getCommandName(String identifier) {
        return this.getCommand(identifier).getString("name");
    }

    /**
     * Used to get a command's aliases from the configuration file.
     *
     * @param identifier The identifier of the command.
     * @return The commands list of alias names.
     */
    public CommandAliases getCommandAliases(String identifier) {
        return new CommandAliases().append(this.getCommand(identifier).getListString("aliases"));
    }

    /**
     * Used to get the command's required permission from the configuration file.
     * If the permission does not exist, it will return null.
     *
     * @param identifier The identifier of the command.
     * @return The commands required permission.
     */
    public String getCommandPermission(String identifier) {
        return this.getCommand(identifier).getString("permission");
    }

    /**
     * Used to get the commands sound from the configuration file.
     *
     * @param identifier The identifier of the command.
     * @return The commands sound that is played when executed.
     */
    public String getCommandSound(String identifier) {
        return this.getCommand(identifier).getString("sound");
    }

    /**
     * Used to get the command's limit.
     *
     * @param identifier The command's identifier.
     * @return The command limit.
     * -1 if there is no command limit.
     */
    public int getCommandLimit(String identifier) {
        return this.getCommand(identifier).getInteger("limit", -1);
    }

    public long getCommandCooldown(String identifier) {
        return this.getCommand(identifier).getLong("cooldown", -1);
    }

    /**
     * Used to get the first command with a certain
     * command type.
     *
     * @param commandType The command type name.
     * @return An instance of a command configuration
     * section with the specified type.
     */
    public ConfigurationSection getCommandFromType(String commandType) {
        for (YamlConfiguration configuration : this.configFileList) {
            for (String identifier : configuration.getKeys()) {
                String type = configuration.getSection(identifier).getString("type");
                if (type == null) continue;
                if (Objects.equals(type, commandType)) return configuration.getSection(identifier);
            }
        }

        return null;
    }

    /**
     * Used to get if the command is enabled.
     *
     * @param identifier The identifier of the command.
     * @return True if the command is enabled.
     */
    public boolean isCommandEnabled(String identifier) {
        return this.getCommand(identifier).getBoolean("enabled", true);
    }
}
