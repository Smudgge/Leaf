package com.github.smuddgge.leaf.commands;

/**
 * Represents a command's status.
 * Returned after executed.
 */
public class CommandStatus {

    private boolean hasRequiredArguments = true;

    /**
     * Used to set required arguments to false.
     *
     * @return This instance.
     */
    public CommandStatus setInvalidArguments() {
        this.hasRequiredArguments = false;

        return this;
    }

    /**
     * Used to get if the command sender has provided
     * the correct arguments.
     *
     * @return False if the arguments are invalid.
     */
    public boolean getRequiredArguments() {
        return this.hasRequiredArguments;
    }
}
