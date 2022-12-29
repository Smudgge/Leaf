package com.github.smuddgge.leaf.inventorys;

import com.github.smuddgge.leaf.datatype.User;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;

import java.util.HashMap;

/**
 * Represents an inventory interface.
 */
public abstract class InventoryInterface {

    protected final Inventory inventory;

    private final HashMap<Integer, Runnable> actions = new HashMap<>();

    /**
     * Used to create an inventory interface.
     *
     * @param user The instance of the user.
     */
    public InventoryInterface(User user) {
        this.inventory = new Inventory(this.getInventoryType());
        System.out.println("create inventory");

        this.inventory.item(0, new ItemStack(ItemType.LIGHT_GRAY_STAINED_GLASS_PANE));

        this.load(user);
        System.out.println("load");

        ProtocolizePlayer player = Protocolize.playerProvider().player(user.getUniqueId());
        player.openInventory(this.inventory);
        System.out.println("open");

        this.inventory.onClick(click -> {
            int slot = click.slot();
            if (!this.actions.containsKey(slot)) return;

            this.actions.get(slot).run();
        });
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

    /**
     * Used to add a action when a slot is clicked.
     *
     * @param index The index of the slot.
     * @param runnable The runnable to execute.
     */
    public void addAction(int index, Runnable runnable) {

    }
}
