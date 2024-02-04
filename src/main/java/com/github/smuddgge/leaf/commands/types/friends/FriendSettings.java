package com.github.smuddgge.leaf.commands.types.friends;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.dependencys.ProtocolizeDependency;
import com.github.smuddgge.leaf.inventorys.inventorys.FriendSettingsInventory;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;

/**
 * <h1>Friend Settings Subcommand Type</h1>
 * Opens a {@link com.github.smuddgge.leaf.inventorys.InventoryInterface}
 * containing the players global friend settings.
 */
public class FriendSettings implements CommandType {

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
        // Check if inventory interface is disabled.
        if (!ProtocolizeDependency.isInventoryEnabled()) {
            MessageManager.warn("Tried to use inventorys when the dependency is not enabled.");
            MessageManager.log("&7" + ProtocolizeDependency.getDependencyMessage());
            return new CommandStatus().error();
        }

        // Open the players friend settings inventory.
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
