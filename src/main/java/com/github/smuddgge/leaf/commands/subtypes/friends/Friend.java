package com.github.smuddgge.leaf.commands.subtypes.friends;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.dependencys.ProtocolizeDependency;
import com.github.smuddgge.leaf.inventorys.inventorys.FriendListInventory;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;

/**
 * <h1>Friends Command Type</h1>
 * Used to execute the list sub command.
 * Also acts as a parent command for the friend subcommands.
 */
public class Friend extends BaseCommandType {

    @Override
    public String getName() {
        return "friends";
    }

    @Override
    public String getSyntax() {
        return "/[name] <optional argument>";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();

        // Check if inventory interface is disabled.
        if (!ProtocolizeDependency.isInventoryEnabled()) {
            FriendList.sendMessage(user, section.getSection("list"), arguments);
            return new CommandStatus();
        }

        // Open the friend list inventory for the user.
        try {
            FriendListInventory friendListInventory = new FriendListInventory(section.getSection("list"), user);
            friendListInventory.open();

        } catch (Exception exception) {
            user.sendMessage(section.getSection("list").getString("error", "{error_colour}Error occurred when opening inventory."));
            MessageManager.warn("Exception occurred when opening a friend list inventory! [/friends]");
            exception.printStackTrace();
        }

        return new CommandStatus();
    }

    @Override
    public void loadSubCommands() {
        this.addSubCommandType(new FriendList());
        this.addSubCommandType(new FriendRequest());
        this.addSubCommandType(new FriendAccept());
        this.addSubCommandType(new FriendSettings());
        this.addSubCommandType(new FriendUnfriend());
        this.addSubCommandType(new FriendOnline());
        this.addSubCommandType(new FriendMessage());
        this.addSubCommandType(new FriendReply());
    }
}
