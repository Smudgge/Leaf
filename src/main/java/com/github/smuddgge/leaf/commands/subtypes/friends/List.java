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
        ConfigurationSection list = section.getSection("list");

        // Check if the user is able to see other friend lists.
        if (list.getKeys().contains("permission_see_any") && !user.hasPermission(list.getString("permission_see_any"))) {
            return null;
        }

        if (Leaf.getDatabase().isDisabled()) return null;

        return new CommandSuggestions().appendDatabasePlayers();
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        ConfigurationSection list = section.getSection("list");

        // Open friend list inventory.
        try {

            // If a player is specified
            if (arguments.length >= 2 && list.getKeys().contains("permission_see_any") &&
                    user.hasPermission(section.getString("permission_see_any"))) {

                // Get the argument for the players name
                String listContextUserName = arguments[1];

                if (Leaf.getDatabase().isDisabled()) return new CommandStatus().databaseDisabled();

                PlayerTable playerTable = (PlayerTable) Leaf.getDatabase().getTable("Player");

                // Check if the user exists in the database
                if (!playerTable.contains(listContextUserName)) {
                    user.sendMessage(section.getString("not_found", "{error_colour}Player could not be found."));
                    return new CommandStatus();
                }

                PlayerRecord playerRecord = (PlayerRecord) playerTable.getRecord("name", listContextUserName).get(0);

                FriendListInventory friendListInventory = new FriendListInventory(
                        section.getSection(this.getName()), user, playerRecord.uuid
                );
                friendListInventory.open();
                return new CommandStatus();
            }

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
