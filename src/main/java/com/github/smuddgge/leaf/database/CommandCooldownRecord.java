package com.github.smuddgge.leaf.database;

import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.database.Record;
import com.github.squishylib.database.annotation.Field;
import com.github.squishylib.database.annotation.Primary;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CommandCooldownRecord implements Record<CommandCooldownRecord> {

    public static final @NotNull String ID_FIELD = "id";
    public static final @NotNull String LAST_EXECUTED_TIMESTAMP_FIELD = "lastExecutedTimestamp";

    @Primary
    @Field(ID_FIELD)
    private @NotNull String id;
    private @Field(LAST_EXECUTED_TIMESTAMP_FIELD) String lastExecutedTimestamp;

    public CommandCooldownRecord(@NotNull final String id) {
        this.id = id;
    }

    public @NotNull String getId() {
        return this.id;
    }

    public long getLastExecutedTimestamp() {
        return Long.parseLong(this.lastExecutedTimestamp);
    }

    public @NotNull CommandCooldownRecord setLastExecutedTimestamp(final long lastExecutedTimestamp) {
        this.lastExecutedTimestamp = String.valueOf(lastExecutedTimestamp);
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        final ConfigurationSection section = new MemoryConfigurationSection();

        section.set(ID_FIELD, this.id);
        section.set(LAST_EXECUTED_TIMESTAMP_FIELD, this.lastExecutedTimestamp);

        return section;
    }

    @Override
    public @NotNull CommandCooldownRecord convert(@NotNull ConfigurationSection section) {

        this.id = Objects.requireNonNull(section.getString(ID_FIELD));
        this.lastExecutedTimestamp = section.getString(LAST_EXECUTED_TIMESTAMP_FIELD);

        return this;
    }
}
