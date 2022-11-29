package com.github.smuddgge.leaf.commands.commands;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.Command;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.datatype.User;

/**
 * Represents the reload command.
 * <ul>
 *     <li>Used to reload the plugins commands, configuration and placeholders.</li>
 * </ul>
 */
public class Reload extends Command {

    @Override
    public String getIdentifier() {
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
    public CommandStatus onConsoleRun(String[] arguments) {
        this.reloadAll();

        String message = ConfigCommands
                .getCommand(this.getIdentifier())
                .getString("message", "{message} Message not configured for [/reload] command.");

        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(String[] arguments, User user) {
        this.reloadAll();

        String message = ConfigCommands
                .getCommand(this.getIdentifier())
                .getString("message", "{message} Message not configured for [/reload] command.");

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

        // Reload the commands and re-register them
        Leaf.reloadCommands();

        MessageManager.log("&f&lReloaded successfully");
    }
}
