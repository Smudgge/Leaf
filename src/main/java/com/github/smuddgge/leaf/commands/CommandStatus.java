package com.github.smuddgge.leaf.commands;

/**
 * Represents a command's status.
 * Returned after a command is executed.
 */
public class CommandStatus {

    private boolean hasIncorrectArguments = false;

    /**
     * Used to set incorrect arguments to true.
     *
     * @return This instance.
     */
    public CommandStatus incorrectArguments() {
        this.hasIncorrectArguments = true;
        return this;
    }

    /**
     * Used to get if the command sender has provided
     * the correct arguments.
     *
     * @return False if the arguments are invalid.
     */
    public boolean hasIncorrectArguments() {
        return this.hasIncorrectArguments;
    }
}
