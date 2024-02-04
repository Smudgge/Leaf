package com.github.smuddgge.leaf.commands.types.whitelist;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the whitelist command type.
 */
public class Whitelist extends BaseCommandType {

    @Override
    public String getName() {
        return "whitelist";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        MessageManager.log(this.getWhitelistConverted(section));
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        user.sendMessage(this.getWhitelistConverted(section));
        return new CommandStatus();
    }

    @Override
    public void loadSubCommands() {
        this.addSubCommandType(new WhitelistAdd());
        this.addSubCommandType(new WhitelistRemove());
        this.addSubCommandType(new WhitelistToggle());
    }

    public @NotNull String getWhitelistConverted(@NotNull ConfigurationSection section) {
        StringBuilder builder = new StringBuilder();

        // Check if there is a header.
        if (section.getKeys().contains("header")) {
            builder.append(section.getAdaptedString("header", "\n"));
            builder.append("\n");
        }

        // Add the players.
        builder.append("&f").append(String.join("&7, &f", Leaf.getWhitelist()));

        // Check if there is a header.
        if (section.getKeys().contains("footer")) {
            builder.append(section.getAdaptedString("footer", "\n"));
        }

        return builder.toString();
    }
}
