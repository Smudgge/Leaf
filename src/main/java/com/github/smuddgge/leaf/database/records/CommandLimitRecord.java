package com.github.smuddgge.leaf.database.records;

import com.github.smuddgge.squishydatabase.record.Field;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;
import net.dv8tion.jda.api.entities.Member;
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

    @Field(type = RecordFieldType.PRIMARY)
    public String primaryKey;

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
    public static @NotNull String createPrimaryKey(@NotNull UUID uuid, @NotNull String commandId) {
        return "minecraft--" + uuid + "--" + commandId;
    }

    /**
     * Used to create the record primary key from
     * the members name and commands id.
     *
     * @param member    The instance of the member.
     * @param commandId The command's identifier.
     * @return The requested primary key.
     */
    public static @NotNull String createPrimaryKey(@NotNull Member member, @NotNull String commandId) {
        return "discord--" + member.getUser().getName() + "--" + commandId;
    }
}
