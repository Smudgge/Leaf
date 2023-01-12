package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.CustomInventory;
import com.github.smuddgge.leaf.inventorys.InventoryItem;
import dev.simplix.protocolize.api.item.ItemStack;

public class Inventory extends BaseCommandType {

    @Override
    public void loadSubCommands() {

    }

    @Override
    public String getName() {
        return "inventory";
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
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {

        try {
            CustomInventory customInventory = new CustomInventory(section, user) {
                @Override
                public ItemStack onLoadItemWithFunction(InventoryItem inventoryItem) {
                    return inventoryItem.getItemStack();
                }
            };

            customInventory.open();

        } catch (Exception exception) {
            user.sendMessage(section.getString("error", "{error_colour}Error occurred when opening inventory."));

            MessageManager.warn("Error occurred when opening inventory!");
            MessageManager.log("&6Command : &e" + section.getString("name", "Null"));
            exception.printStackTrace();
        }

        return new CommandStatus();
    }
}
