package com.github.smuddgge.leaf.commands.subtypes.friends;

import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.inventorys.FriendRequestInventoryBeta;

public class Requests implements CommandType {

    @Override
    public String getName() {
        return "requests";
    }

    @Override
    public String getSyntax() {
        return "/[parent] [name]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {

        FriendRequestInventoryBeta friendRequestInventory = new FriendRequestInventoryBeta(section.getSection("requests"));
        friendRequestInventory.loadAndOpen(user);

        return new CommandStatus();
    }
}
