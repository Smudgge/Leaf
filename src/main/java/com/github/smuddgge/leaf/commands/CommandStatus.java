package com.github.smuddgge.leaf.commands;

/**
 * <h1>Represents a command's status.</h1>
 * Returned after a command is executed.
 */
public class CommandStatus {

    private boolean hasError = false;
    private boolean hasIncorrectArguments = false;
    private boolean hasDatabaseDisabled = false;
    private boolean hasDatabaseEmpty = false;
    private boolean hasPlayerCommand = false;

    /**
     * Used to set error to true.
     *
     * @return This instance.
     */
    public CommandStatus error() {
        this.hasError = true;
        return this;
    }

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
     * Used to set database disabled to true.
     *
     * @return This instance.
     */
    public CommandStatus databaseDisabled() {
        this.hasDatabaseDisabled = true;
        return this;
    }

    /**
     * Used to set database empty to true.
     *
     * @return This instance.
     */
    public CommandStatus databaseEmpty() {
        this.hasDatabaseEmpty = true;
        return this;
    }

    /**
     * Used to set player command to true.
     *
     * @return This instance.
     */
    public CommandStatus playerCommand() {
        this.hasPlayerCommand = true;
        return this;
    }

    /**
     * Used to get if an error occurred and an error
     * message should be sent.
     *
     * @return True if an error occurred.
     */
    public boolean hasError() {
        return this.hasError;
    }

    /**
     * Used to get if the command sender has provided
     * the correct arguments.
     *
     * @return True if the arguments are invalid.
     */
    public boolean hasIncorrectArguments() {
        return this.hasIncorrectArguments;
    }

    /**
     * Used to get if the database is disabled.
     *
     * @return True if the database is disabled;
     */
    public boolean hasDatabaseDisabled() {
        return this.hasDatabaseDisabled;
    }

    /**
     * Used to get if the database was empty.
     *
     * @return True if the database was empty.
     */
    public boolean hasDatabaseEmpty() {
        return this.hasDatabaseEmpty;
    }

    /**
     * Used to get if the command is only for the player to run.
     *
     * @return True if it's a player only command.
     */
    public boolean hasPlayerCommand() {
        return this.hasPlayerCommand;
    }
}
