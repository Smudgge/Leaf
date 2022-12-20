package com.github.smuddgge.leaf.commands.subtypes.friends;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;

public class Request implements CommandType {

    @Override
    public String getName() {
        return "request";
    }

    @Override
    public String getSyntax() {
        return "/[parent] [name] [player] <optional message>";
    }

    @Override
    public CommandSuggestions getSuggestions(User user) {
        return new CommandSuggestions().appendPlayers();
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        MessageManager.log("testsub");
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        return new CommandStatus();
    }
}
