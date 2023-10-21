package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.datatype.User;
import org.jetbrains.annotations.NotNull;

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
    private boolean hasNoPermission = false;
    private boolean hasIsLimited = false;

    private boolean hasStopIncreaseLimit = false;

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
     * Used to set no permission to true.
     *
     * @return This instnace.
     */
    public CommandStatus noPermission() {
        this.hasNoPermission = true;
        return this;
    }

    /**
     * Used to set is limited to true.
     *
     * @return This instance.
     */
    public CommandStatus isLimited() {
        this.hasIsLimited = true;
        return this;
    }

    /**
     * Used to stop the limit from increasing.
     *
     * @return This instance.
     */
    public CommandStatus stopIncreaseLimit() {
        this.hasStopIncreaseLimit = true;
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

    /**
     * Used to get if the user doesn't have permission.
     *
     * @return True if the player doesn't have permission.
     */
    public boolean hasNoPermission() {
        return this.hasNoPermission;
    }

    /**
     * Used to check if the user has been limited.
     *
     * @return True if the player has been limited.
     */
    public boolean hasIsLimited() {
        return this.hasIsLimited;
    }

    /**
     * Used to check if the increase of the command limit
     * should be stopped.
     *
     * @return True if the increase of the command limit
     * should be stopped.
     */
    public boolean hasStopIncreaseLimit() {
        return this.hasStopIncreaseLimit;
    }

    /**
     * Used to get the error message.
     *
     * @return The message.
     */
    public String getMessage() {
        if (this.hasError()) return ConfigMessages.getError();
        if (this.hasDatabaseDisabled()) return ConfigMessages.getDatabaseDisabled();
        if (this.hasDatabaseEmpty()) return ConfigMessages.getDatabaseEmpty();
        if (this.hasPlayerCommand()) return ConfigMessages.getPlayerCommand();
        if (this.hasNoPermission()) return ConfigMessages.getNoPermission();
        if (this.hasIsLimited()) return ConfigMessages.getIsLimited();
        return null;
    }

    /**
     * Used to increase the command limit of a command in
     * regard to this command status.
     *
     * @param user The instance of the user.
     * @param command The command's instance.
     * @return This instance.
     */
    public @NotNull CommandStatus increaseLimit(@NotNull User user, @NotNull Command command) {

        // Check if the increase has been stopped.
        if (this.hasStopIncreaseLimit()) return this;

        // Check if the command doesn't have a limit.
        if (!command.hasLimit()) return this;

        // Increase the limit.
        user.increaseAmountExecuted(command.getIdentifier());
        return this;
    }
}
