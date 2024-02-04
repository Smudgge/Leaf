package com.github.smuddgge.leaf.commands.types.whitelist;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to add a player to the whitelist.
 */
public class WhitelistAdd implements CommandType {

    @Override
    public String getName() {
        return "add";
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

        list.add(playerName.toLowerCase());

        Leaf.getWhitelistConfig().set("players", list);
        Leaf.getWhitelistConfig().save();

        MessageManager.log(
                section.getAdaptedString("message", "\n", "&7Added %player% to the whitelist.")
                        .replace("%player%", playerName)
        );
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        final String playerName = arguments[1];

        final List<String> list = Leaf.getWhitelistConfig().getListString("players", new ArrayList<>());

        list.add(playerName.toLowerCase());

        Leaf.getWhitelistConfig().set("players", list);
        Leaf.getWhitelistConfig().save();

        user.sendMessage(
                section.getAdaptedString("message", "\n", "&7Added %player% to the whitelist.")
                        .replace("%player%", playerName)
        );
        return new CommandStatus();
    }
}
