package com.github.smuddgge.leaf.commands.types.whitelist;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to remove someone from the whitelist.
 */
public class WhitelistRemove implements CommandType {

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getSyntax() {
        return "/[parent] [name] <player>";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        if (Leaf.getDatabase().isEnabled()) return new CommandSuggestions().appendDatabasePlayers();
        return new CommandSuggestions().appendPlayers();
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        final String playerName = arguments[1];

        final List<String> list = Leaf.getWhitelistConfig().getListString("players", new ArrayList<>());

        list.remove(playerName);

        if (Leaf.getDatabase().isEnabled()) {
            final PlayerRecord record = Leaf.getDatabase()
                    .getTable(PlayerTable.class)
                    .getFirstRecord(new Query().match("name", playerName));

            if (record != null) list.remove(record.uuid);
        }

        Leaf.getWhitelistConfig().set("players", list);
        Leaf.getWhitelistConfig().save();

        MessageManager.log(
                section.getAdaptedString("message", "\n", "&7Removed %player% from the whitelist.")
                        .replace("%player%", playerName)
        );
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        final String playerName = arguments[1];

        final List<String> list = Leaf.getWhitelistConfig().getListString("players", new ArrayList<>());

        list.remove(playerName);

        if (Leaf.getDatabase().isEnabled()) {
            final PlayerRecord record = Leaf.getDatabase()
                    .getTable(PlayerTable.class)
                    .getFirstRecord(new Query().match("name", playerName));

            if (record != null) list.remove(record.uuid);
        }

        Leaf.getWhitelistConfig().set("players", list);
        Leaf.getWhitelistConfig().save();

        user.sendMessage(
                section.getAdaptedString("message", "\n", "&7Removed %player% from the whitelist.")
                        .replace("%player%", playerName)
        );
        return new CommandStatus();
    }
}
