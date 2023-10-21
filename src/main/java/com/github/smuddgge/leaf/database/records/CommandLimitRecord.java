package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldAnnotation;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a command limit record.
 * Used to limit the amount of times a player
 * can execute a command.
 * <p>
 * Implemented with version 4.3.0
 */
public class CommandLimitRecord extends Record {

    @RecordFieldAnnotation(type = RecordFieldType.PRIMARY)
    public String uuidPlusCommand;

    public String amountExecuted;

    /**
     * Used to get the amount of times the
     * player has executed this command.
     *
     * @return The amount executed.
     */
    public int getAmountExecuted() {
        return Integer.parseInt(this.amountExecuted);
    }

    /**
     * Used to create the records primary key from
     * the players uuid and the commands id.
     *
     * @param uuid      The players uuid.
     * @param commandId The command's id.
     * @return The requested primary key.
     */
    public static String createUuidPlusCommand(@NotNull UUID uuid, @NotNull String commandId) {
        return uuid + "--" + commandId;
    }
}
