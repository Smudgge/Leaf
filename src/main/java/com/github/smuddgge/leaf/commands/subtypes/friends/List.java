package com.github.smuddgge.leaf.commands.subtypes.friends;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.inventorys.FriendListInventory;

public class List implements CommandType {

    @Override
    public String getName() {
        return "list";
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

        // Open friend list inventory.
        try {
            FriendListInventory friendListInventory = new FriendListInventory(section.getSection(this.getName()), user);
            friendListInventory.open();
        } catch (Exception exception) {
            user.sendMessage(section.getSection("list").getString("error", "{error_colour}Error occurred when opening inventory."));

            MessageManager.warn("Exception occurred when opening a friend list inventory!");
            exception.printStackTrace();
        }

        return new CommandStatus();
    }
}
