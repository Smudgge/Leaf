package com.github.smuddgge.leaf.inventorys;

/**
 * Represents an item action.
 */
public class Action {

    private int slot;
    private Runnable runnable;

    /**
     * Used to create an item action.
     *
     * @param slot The slot number.
     * @param runnable The runnable.
     */
    public Action(int slot, Runnable runnable) {
        this.slot = slot;
        this.runnable = runnable;
    }

    /**
     * Used to get the slot.
     *
     * @return The slot number.
     */
    public int getSlot() {
        return this.slot;
    }

    /**
     * Used to run the action.
     */
    public void run() {
        this.runnable.run();
    }
}
