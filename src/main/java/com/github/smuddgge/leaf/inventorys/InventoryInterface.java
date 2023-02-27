package com.github.smuddgge.leaf.inventorys;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an inventory interface.
 */
public abstract class InventoryInterface {

    protected Inventory inventory;
    protected User user;

    private List<Action> actions = new ArrayList<>();

    /**
     * Used to create an inventory interface.
     *
     * @param user The user that will open the inventory.
     */
    public InventoryInterface(User user) {
        this.user = user;
    }

    /**
     * Used to load the inventory and open it for the player.
     *
     * @return This instance.
     */
    public InventoryInterface open() {
        this.inventory = new Inventory(this.getInventoryType());
        this.inventory.title(MessageManager.convertToLegacy(this.getTitle()));
        this.load();

        ProtocolizePlayer player = Protocolize.playerProvider().player(this.user.getUniqueId());
        player.openInventory(this.inventory);

        this.inventory.onClick(click -> {
            int slot = click.slot();

            // Loop though the actions in this inventory.
            for (Action action : this.actions) {
                if (action.getSlot() != slot) continue;
                action.run();
            }
        });

        return this;
    }

    /**
     * Used to close the inventory the player has open.
     *
     * @return This instance.
     */
    public InventoryInterface close() {
        ProtocolizePlayer player = Protocolize.playerProvider().player(this.user.getUniqueId());
        player.closeInventory();
        return this;
    }

    /**
     * Used to add an action when a slot is clicked.
     *
     * @param slot     The slot number
     * @param runnable The runnable to execute.
     */
    public void addAction(int slot, Runnable runnable) {
        Action action = new Action(slot, runnable);
        this.actions.add(action);
    }

    /**
     * Used to add a command to an item slot.
     *
     * @param command The command to add.
     * @param slot    The slot number.
     */
    public void addCommand(String command, int slot) {
        this.addAction(slot, () -> this.user.executeCommand(
                PlaceholderManager.parse(command, null, this.user)
        ));
    }

    /**
     * Used to add a command to the inventory when an item is clicked.
     *
     * @param command       The command to execute.
     * @param inventoryItem The inventory item.
     */
    public void addCommand(String command, InventoryItem inventoryItem) {
        for (int slot : inventoryItem.getSlots(this.getInventoryType())) {
            this.addCommand(command, slot);
        }
    }

    /**
     * Used to add a list of commands to the item.
     *
     * @param commands      The list of commands
     * @param inventoryItem The instance of the item.
     */
    public void addCommandList(List<String> commands, InventoryItem inventoryItem) {
        for (String command : commands) {
            this.addCommand(command, inventoryItem);
        }
    }

    /**
     * Used to reset the actions the inventory contains.
     */
    protected void resetActions() {
        this.actions = new ArrayList<>();
    }

    /**
     * Used to get the inventory type.
     *
     * @return The type of inventory.
     */
    public abstract InventoryType getInventoryType();

    /**
     * Used to get the inventory's title.
     *
     * @return The title of the inventory.
     */
    public abstract String getTitle();

    /**
     * Used to create the inventory.
     */
    protected abstract void load();
}
