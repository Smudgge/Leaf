package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.records.CommandLimitRecord;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents the command limit table.
 * Used to check if a player has reached the limit
 * on the number of times they can execute a command.
 * <p>
 * Implemented with version 4.3.0
 */
public class CommandLimitTable extends TableAdapter<CommandLimitRecord> {

    @Override
    public @NotNull String getName() {
        return "CommandLimit";
    }

    /**
     * Used to get the amount of times a command
     * has been executed by a player.
     *
     * @param uuid      The players uuid.
     * @param commandId The id of the command.
     * @return The amount executed.
     */
    public int getAmountExecuted(@NotNull UUID uuid, @NotNull String commandId) {
        CommandLimitRecord record = this.getFirstRecord(
                new Query().match(
                        "primaryKey",
                        CommandLimitRecord.createPrimaryKey(uuid, commandId)
                )
        );

        return record == null ? 0 : record.getAmountExecuted();
    }

    /**
     * Used to get the amount of times a command
     * has been executed by a discord member.
     *
     * @param member    The discord member.
     * @param commandId The command's identifier.
     * @return The amount executed.
     */
    public int getAmountExecuted(@NotNull Member member, @NotNull String commandId) {
        CommandLimitRecord record = this.getFirstRecord(
                new Query().match(
                        "primaryKey",
                        CommandLimitRecord.createPrimaryKey(member, commandId)
                )
        );

        return record == null ? 0 : record.getAmountExecuted();
    }

    /**
     * Used to increase the amount of times a player
     * has executed a specific command.
     *
     * @param uuid      The players uuid.
     * @param commandId The command's id.
     */
    public void increaseAmountExecuted(@NotNull UUID uuid, @NotNull String commandId) {

        // Attempt to get the record.
        CommandLimitRecord record = this.getFirstRecord(
                new Query().match(
                        "primaryKey",
                        CommandLimitRecord.createPrimaryKey(uuid, commandId)
                )
        );

        // Check if the record doesn't exist.
        if (record == null) {
            record = new CommandLimitRecord();
            record.primaryKey = CommandLimitRecord.createPrimaryKey(uuid, commandId);
            record.amountExecuted = "0";
        }

        // Increase the amount executed.
        record.amountExecuted = String.valueOf(record.getAmountExecuted() + 1);

        // Update in database.
        this.insertRecord(record);
    }

    /**
     * Used to increase the amount of times a discord
     * member has executed a specific command.
     *
     * @param member    The instance of the member.
     * @param commandId The command's identifier
     */
    public void increaseAmountExecuted(@NotNull Member member, @NotNull String commandId) {

        // Attempt to get the record.
        CommandLimitRecord record = this.getFirstRecord(
                new Query().match(
                        "primaryKey",
                        CommandLimitRecord.createPrimaryKey(member, commandId)
                )
        );

        // Check if the record doesn't exist.
        if (record == null) {
            record = new CommandLimitRecord();
            record.primaryKey = CommandLimitRecord.createPrimaryKey(member, commandId);
            record.amountExecuted = "0";
        }

        // Increase the amount executed.
        record.amountExecuted = String.valueOf(record.getAmountExecuted() + 1);

        // Update in database.
        this.insertRecord(record);
    }
}
