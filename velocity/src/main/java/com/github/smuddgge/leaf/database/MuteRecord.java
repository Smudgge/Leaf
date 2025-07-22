package com.github.smuddgge.leaf.database;

import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.database.Record;
import com.github.squishylib.database.annotation.Field;
import com.github.squishylib.database.annotation.Primary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MuteRecord implements Record<MuteRecord> {

    public static final @NotNull String UUID_FIELD = "uuid";
    public static final @NotNull String TIMESTAMP_END_FIELD = "timeStampEnd";

    @Primary
    @Field(UUID_FIELD)
    public @NotNull String uuid;

    public @Field(TIMESTAMP_END_FIELD) String timeStampEnd;

    public MuteRecord(final @NotNull String uuid) {
        this.uuid = uuid;
    }

    public @NotNull String getUuid() {
        return this.uuid;
    }

    public @Nullable String getTimeStampEnd() {
        return this.timeStampEnd;
    }

    public @NotNull MuteRecord setTimeStampEnd(@Nullable final String timeStampEnd) {
        this.timeStampEnd = timeStampEnd;
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        final ConfigurationSection section = new MemoryConfigurationSection();

        section.set(UUID_FIELD, this.uuid);
        section.set(TIMESTAMP_END_FIELD, this.timeStampEnd);

        return section;
    }

    @Override
    public @NotNull MuteRecord convert(@NotNull ConfigurationSection section) {

        this.uuid = Objects.requireNonNull(section.getString(UUID_FIELD));
        this.timeStampEnd = section.getString(TIMESTAMP_END_FIELD);

        return this;
    }
}
