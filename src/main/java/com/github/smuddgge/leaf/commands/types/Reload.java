package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.configuration.ConfigDatabase;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;

/**
 * Represents the reload command type.
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
        this.reloadAll();

        String message = section.getString("message", "{message} Reloaded all configs! <3");

        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (!this.reloadAll()) {
            user.sendMessage(section.getString("error", "{error_colour}An error occurred and the reloading was aborted!"));
            return new CommandStatus();
        }

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
            ConfigCommands.reload();
            ConfigMessages.reload();
            ConfigDatabase.reload();

        } catch (Exception exception) {
            MessageManager.warn("Error occurred when reloading configs and aborted the reload.");
            exception.printStackTrace();
            return false;
        }

        // Unregister the commands
        Leaf.getCommandHandler().unregister();

        // Reload the commands and re-register them
        Leaf.reloadCommands();

        MessageManager.log("&f&lReloaded successfully");
        return true;
    }

    @Override
    public void loadSubCommands() {

    }
}
