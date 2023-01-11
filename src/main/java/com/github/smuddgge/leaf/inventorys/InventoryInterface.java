package com.github.smuddgge.leaf.inventorys;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.datatype.User;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.inventory.InventoryType;

import java.util.HashMap;

/**
 * Represents an inventory interface.
 */
public abstract class InventoryInterface {

    protected Inventory inventory;
    protected User user;

    private final HashMap<Integer, Runnable> actions = new HashMap<>();

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
            if (!this.actions.containsKey(slot)) return;

            this.actions.get(slot).run();
        });

        return this;
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

    /**
     * Used to add a action when a slot is clicked.
     *
     * @param index    The index of the slot.
     * @param runnable The runnable to execute.
     */
    public void addAction(int index, Runnable runnable) {
        this.actions.put(index, runnable);
    }
}
