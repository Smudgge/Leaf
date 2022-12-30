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
    public CommandSuggestions getSuggestions(User user) {
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
        this.reloadAll();

        String message = section.getString("message", "{message} Reloaded all configs! <3");

        user.sendMessage(message);

        return new CommandStatus();
    }

    /**
     * Used to reload the plugin.
     */
    private void reloadAll() {
        MessageManager.log("&f&lReloading");

        // Unregister the commands
        Leaf.getCommandHandler().unregister();

        // Reload configs
        ConfigCommands.reload();
        ConfigMessages.reload();
        ConfigDatabase.reload();

        // Reload the commands and re-register them
        Leaf.reloadCommands();

        MessageManager.log("&f&lReloaded successfully");
    }

    @Override
    public void loadSubCommands() {

    }
}
