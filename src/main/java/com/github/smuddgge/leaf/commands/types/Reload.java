package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigDatabase;
import com.github.smuddgge.leaf.configuration.ConfigurationManager;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;

/**
 * <h1>Reload Command Type</h1>
 * Used to reload this plugin.
 * The command reloads the configuration files
 * and the commands.
 */
public class Reload extends BaseCommandType {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        // Reload the plugin.
        // When aborted it will already log the message in console.
        boolean success = this.reloadAll();

        // Check if the reloading was aborted.
        if (!success) return new CommandStatus();

        // Get the message and log it in console.
        String message = section.getString("message", "{message} Reloaded all configs! <3");
        MessageManager.log(message);
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        // Attempt to reload the plugin.
        boolean success = this.reloadAll();

        // Check if the reloading was aborted.
        if (!success) {
            user.sendMessage(section.getString("error", "{error_colour}An error occurred and the reloading was aborted!"));
            return new CommandStatus();
        }

        // Get the message and send it to the user.
        String message = section.getString("message", "{message} Reloaded all configs! <3");
        user.sendMessage(message);
        return new CommandStatus();
    }

    /**
     * Used to reload the plugin.
     */
    private boolean reloadAll() {
        MessageManager.log("&f&lReloading");

        try {

            // Reload configs
            ConfigurationManager.reload();

        } catch (Exception exception) {
            MessageManager.warn("Error occurred when reloading configs and aborted the reload.");
            exception.printStackTrace();
            return false;
        }

        // Unregister the commands
        Leaf.getCommandHandler().unregister();

        // Reload the commands and re-register them
        Leaf.reloadCommands();

        // Reload database.
        Leaf.getDatabase().setDebugMode(ConfigDatabase.isDebugMode());

        MessageManager.log("&f&lReloaded successfully");
        return true;
    }
}
