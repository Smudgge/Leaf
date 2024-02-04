package com.github.smuddgge.leaf.commands.types.friends;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.dependencys.ProtocolizeDependency;
import com.github.smuddgge.leaf.inventorys.inventorys.FriendOnlineInventory;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;

/**
 * <h1>Friend Online Subcommand Type</h1>
 */
public class FriendOnline implements CommandType {

    @Override
    public String getName() {
        return "online";
    }

    @Override
    public String getSyntax() {
        return "/[parent] [name]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        ConfigurationSection listSection = section.getSection(this.getName());

        // Check if the user is not allowed see other players friend lists.
        if (listSection.getKeys().contains("permission_see_any")
                && !user.hasPermission(listSection.getString("permission_see_any"))) return null;

        // Check if the database is disabled.
        if (Leaf.isDatabaseDisabled()) return null;

        // Return all players in the database.
        return new CommandSuggestions().appendDatabasePlayers();
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        // Check if inventory interface is disabled.
        if (!ProtocolizeDependency.isInventoryEnabled()) {
            MessageManager.warn("Tried to use inventorys when the dependency is not enabled.");
            MessageManager.log("&7" + ProtocolizeDependency.getDependencyMessage());
            return new CommandStatus().error();
        }

        ConfigurationSection listSection = section.getSection(this.getName());

        // If a player is specified, and they have permission to see
        // other players friend lists.
        if (arguments.length >= 2
                && listSection.getKeys().contains("permission_see_any")
                && user.hasPermission(listSection.getString("permission_see_any"))) {

            // Check if the database is disabled.
            if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();
            PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);

            // Get the given argument.
            // This will be the owner of the friend list.
            String friendListOwnerName = arguments[1];

            // Get the players details.
            PlayerRecord playerRecord = playerTable.getFirstRecord(new Query().match("name", friendListOwnerName));

            // Check if the user exists in the database.
            if (playerRecord == null) {
                user.sendMessage(section.getString("not_found", "{error_colour}Player could not be found."));
                return new CommandStatus();
            }

            try {
                // Try to open the friend list inventory.
                new FriendOnlineInventory(section.getSection(this.getName()), user, playerRecord.uuid).open();

            } catch (Exception exception) {
                user.sendMessage(listSection.getString("error", "{error_colour}Error occurred when opening inventory."));
                MessageManager.warn("Exception occurred when opening a another players friend list as a inventory!");
                exception.printStackTrace();
            }

            return new CommandStatus();
        }

        try {
            // Try to open the friend list inventory.
            new FriendOnlineInventory(section.getSection(this.getName()), user).open();

        } catch (Exception exception) {
            user.sendMessage(listSection.getString("error", "{error_colour}Error occurred when opening inventory."));
            MessageManager.warn("Exception occurred when opening a friend list inventory normally!");
            exception.printStackTrace();
        }

        return new CommandStatus();
    }
}
