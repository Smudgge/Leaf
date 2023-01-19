package com.github.smuddgge.leaf.commands.subtypes.friends;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.inventorys.FriendListInventory;
import com.github.smuddgge.leaf.inventorys.inventorys.FriendSettingsInventory;

public class Settings implements CommandType {

    @Override
    public String getName() {
        return "settings";
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

        // Open friends settings inventory.
        try {

            FriendSettingsInventory friendSettingsInventory = new FriendSettingsInventory(section.getSection(this.getName()), user);
            friendSettingsInventory.open();

        } catch (Exception exception) {
            user.sendMessage(section.getSection(this.getName()).getString("error", "{error_colour}Error occurred when opening inventory."));

            MessageManager.warn("Exception occurred when opening a friend list inventory!");
            exception.printStackTrace();
        }
        return new CommandStatus();
    }
}
