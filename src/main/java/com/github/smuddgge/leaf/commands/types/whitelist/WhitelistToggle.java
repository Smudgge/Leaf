package com.github.smuddgge.leaf.commands.types.whitelist;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;

/**
 * Used to toggle the whitelist.
 */
public class WhitelistToggle implements CommandType {

    @Override
    public String getName() {
        return "toggle";
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
        boolean toggle = !Leaf.getWhitelistConfig().getBoolean("enabled");

        Leaf.getWhitelistConfig().set("enabled", toggle);
        Leaf.getWhitelistConfig().save();

        MessageManager.log(
                section.getAdaptedString("message", "\n", "&7The whitelist has been toggled %toggle%.")
                        .replace("%toggle%", Boolean.toString(toggle))
        );
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        boolean toggle = !Leaf.getWhitelistConfig().getBoolean("enabled");

        Leaf.getWhitelistConfig().set("enabled", toggle);
        Leaf.getWhitelistConfig().save();

        user.sendMessage(
                section.getAdaptedString("message", "\n", "&7The whitelist has been toggled %toggle%.")
                        .replace("%toggle%", Boolean.toString(toggle))
        );
        return new CommandStatus();
    }
}
