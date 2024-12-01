package com.github.smuddgge.leaf.database;

import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.database.Record;
import com.github.squishylib.database.annotation.Field;
import com.github.squishylib.database.annotation.Primary;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class CommandLimitRecord implements Record<CommandLimitRecord> {

    public static final @NotNull String ID_FIELD = "primaryKey";
    public static final @NotNull String AMOUNT_EXECUTED_FIELD = "amountExecuted";

    @Primary
    @Field(ID_FIELD)
    private @NotNull String id;
    private @Field(AMOUNT_EXECUTED_FIELD) String amountExecuted;

    public CommandLimitRecord(@NotNull final String id) {
        this.id = id;
        this.amountExecuted = "0";
    }

    public @NotNull String getId() {
        return this.id;
    }

    public int getAmountExecuted() {
        return Integer.parseInt(this.amountExecuted);
    }

    public @NotNull CommandLimitRecord setAmountExecuted(final int amountExecuted) {
        this.amountExecuted = String.valueOf(amountExecuted);
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        final ConfigurationSection section = new MemoryConfigurationSection();

        section.set(ID_FIELD, this.id);
        section.set(AMOUNT_EXECUTED_FIELD, this.amountExecuted);

        return section;
    }

    @Override
    public @NotNull CommandLimitRecord convert(@NotNull ConfigurationSection section) {

        this.id = Objects.requireNonNull(section.getString(ID_FIELD));
        this.amountExecuted = section.getString(AMOUNT_EXECUTED_FIELD);

        return this;
    }

    /**
     * Used to create the records primary key from
     * the players uuid and the commands id.
     *
     * @param uuid      The players uuid.
     * @param commandId The command's id.
     * @return The requested primary key.
     */
    public static @NotNull String createId(@NotNull UUID uuid, @NotNull String commandId) {
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
    public static @NotNull String createId(@NotNull Member member, @NotNull String commandId) {
        return "discord--" + member.getUser().getName() + "--" + commandId;
    }
}
