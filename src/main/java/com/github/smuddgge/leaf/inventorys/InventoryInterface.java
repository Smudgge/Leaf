package com.github.smuddgge.leaf.inventorys;

import com.github.smuddgge.leaf.datatype.User;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.inventory.InventoryType;

/**
 * Represents an inventory interface.
 */
public abstract class InventoryInterface {

    protected final Inventory inventory;

    /**
     * Used to create an inventory interface.
     *
     * @param user The instance of the user.
     */
    public InventoryInterface(User user) {
        this.inventory = new Inventory(this.getInventoryType());
        this.load(user);

        ProtocolizePlayer player = Protocolize.playerProvider().player(user.getUniqueId());
        player.openInventory(this.inventory);
    }

    /**
     * Used to get the inventory type.
     *
     * @return The type of inventory.
     */
    public abstract InventoryType getInventoryType();

    /**
     * Used to create the inventory.
     *
     * @param user The instance of the user
     */
    protected abstract void load(User user);
}
