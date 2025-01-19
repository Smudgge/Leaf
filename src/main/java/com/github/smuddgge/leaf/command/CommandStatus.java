package com.github.smuddgge.leaf.command;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.database.CommandCooldownRecord;
import com.github.smuddgge.leaf.database.CommandCooldownTable;
import com.github.smuddgge.leaf.database.CommandLimitRecord;
import com.github.smuddgge.leaf.database.CommandLimitTable;
import com.github.smuddgge.leaf.user.PlayerUser;
import com.github.smuddgge.leaf.user.User;
import com.github.squishylib.database.Query;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Represents a command's status.</h1>
 * Returned after a command is executed.
 */
public class CommandStatus {

    private final @NotNull List<Status> statusList;

    public enum Status {
        ERROR,
        INCORRECT_ARGUMENTS,
        DATABASE_DISABLED,
        DATABASE_EMPTY,
        PLAYER_COMMAND_ONLY,
        NO_PERMISSION,
        IS_LIMITED,
        IS_ON_COOLDOWN,
        STOP_INCREASE_LIMIT
    }

    public CommandStatus() {
        this.statusList = new ArrayList<>();
    }

    public @NotNull CommandStatus set(@NotNull Status status) {
        this.statusList.add(status);
        return this;
    }

    public @NotNull CommandStatus merge(@NotNull CommandStatus status) {
        this.statusList.addAll(status.statusList);
        return this;
    }

    public boolean has(@NotNull Status status) {
        return this.statusList.contains(status);
    }

    public @Nullable String getFirstMessage() {
        if (this.statusList.isEmpty()) return null;

        return switch (this.statusList.get(0)) {
            case ERROR -> Leaf.get().getMessagesConfig().error();
            case INCORRECT_ARGUMENTS -> Leaf.get().getMessagesConfig().incorrectArguments();
            case DATABASE_EMPTY -> Leaf.get().getMessagesConfig().databaseEmpty();
            case PLAYER_COMMAND_ONLY -> Leaf.get().getMessagesConfig().playerCommand();
            case NO_PERMISSION -> Leaf.get().getMessagesConfig().noPermission();
            case IS_LIMITED -> Leaf.get().getMessagesConfig().isLimited();
            case IS_ON_COOLDOWN -> Leaf.get().getMessagesConfig().onCooldown();
            default -> null;
        };
    }

    /**
     * Used to increase the number of command executions
     * for a command in regard to this command status.
     *
     * @param user    The instance of the user.
     * @param command The command's instance.
     * @return This instance.
     */
    public @NotNull CommandStatus increaseExecutions(@NotNull User user, @NotNull Command command) {

        // Is the user a player?
        if (!(user instanceof PlayerUser player)) return this;

        // Check if the increase has been stopped.
        if (this.has(Status.STOP_INCREASE_LIMIT)) return this;

        // Check if the command doesn't have a limit.
        if (!command.hasLimit()) return this;

        // Increase the limit if database is enabled.
        player.increaseAmountExecuted(command.getIdentifier());
        return this;
    }

    /**
     * Used to increase the number of command executions
     * for a command in regard to this command status.
     *
     * @param member  The instance of the discord member.
     * @param command The instance of the command.
     * @return This instance.
     */
    public @NotNull CommandStatus increaseExecutions(@NotNull Member member, @NotNull Command command) {

        // Check if the increase has been stopped.
        if (this.has(Status.STOP_INCREASE_LIMIT)) return this;

        // Check if the command doesn't have a limit.
        if (command.getSection().getSection("discord_bot")
                .getInteger("limit", -1) == -1) return this;

        // Check if the database is disabled.
        if (Leaf.get().getDatabaseConfig().isEnabled()) return this;

        // Increase amount executed.
        Leaf.get().getDatabase()
                .getTable(CommandLimitTable.class)
                .resolveRecord(
                        new Query().match(
                                CommandLimitRecord.ID_FIELD,
                                CommandLimitRecord.createId(member, command.getIdentifier())
                        ),
                        CommandLimitRecord::increaseAmountExecuted
                );
        return this;
    }

    public @NotNull CommandStatus applyCooldownIfNeeded(@NotNull User user, @NotNull Command command) {

        // Is the user a player?
        if (!(user instanceof PlayerUser player)) return this;

        // Has the command got a cooldown?
        if (!command.hasCooldown()) return this;

        // Is the command on cooldown already?
        if (this.has(Status.IS_ON_COOLDOWN)) return this;

        // Update cooldown timestamp.
        Leaf.get().getDatabase()
                .getTable(CommandCooldownTable.class)
                .resolveRecord(
                        new Query().match(
                                CommandCooldownRecord.ID_FIELD,
                                CommandCooldownRecord.createId(user.getUuid(), command.getIdentifier())
                        ),
                        CommandCooldownRecord::setTimestampToNow
                );

        return this;
    }

    public @NotNull CommandStatus applyCooldownIfNeeded(@NotNull Member member, @NotNull Command command) {

        // Has the command got a cooldown?
        if (!command.hasCooldown()) return this;

        // Is the command on cooldown already?
        if (this.has(Status.IS_ON_COOLDOWN)) return this;

        // Update cooldown timestamp.
        Leaf.get().getDatabase()
                .getTable(CommandCooldownTable.class)
                .resolveRecord(
                        new Query().match(
                                CommandCooldownRecord.ID_FIELD,
                                CommandCooldownRecord.createId(member, command.getIdentifier())
                        ),
                        CommandCooldownRecord::setTimestampToNow
                );

        return this;
    }
}
