package com.github.smuddgge.leaf.database;

import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.database.Record;
import com.github.squishylib.database.annotation.Field;
import com.github.squishylib.database.annotation.Foreign;
import com.github.squishylib.database.annotation.Primary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class MessageRecord implements Record<MessageRecord> {

    public static final @NotNull String UUID_FIELD = "uuid";
    public static final @NotNull String MESSAGE_FIELD = "message";
    public static final @NotNull String DATE_FIELD = "date";
    public static final @NotNull String TIMESTAMP_FIELD = "timeStamp";
    public static final @NotNull String FROM_PLAYER_UUID_FIELD = "fromPlayerUuid";
    public static final @NotNull String TO_PLAYER_UUID_FIELD = "toPlayerUuid";

    @Primary
    @Field(UUID_FIELD)
    public @NotNull String uuid;

    public @Field(MESSAGE_FIELD) String message;
    public @Field(DATE_FIELD) String date;
    public @Field(TIMESTAMP_FIELD) String timeStamp;

    @Field(FROM_PLAYER_UUID_FIELD)
    @Foreign(table = "Player", tableField = "uuid")
    public String fromPlayerUuid;

    @Field(TO_PLAYER_UUID_FIELD)
    @Foreign(table = "Player", tableField = "uuid")
    public String toPlayerUuid;

    public MessageRecord(final @NotNull String uuid) {
        this.uuid = uuid;
    }

    public @NotNull UUID getUuid() {
        return UUID.fromString(uuid);
    }

    public @Nullable String getMessage() {
        return this.message;
    }

    public @NotNull MessageRecord setMessage(@Nullable final String message) {
        this.message = message;
        return this;
    }

    public @Nullable String getDate() {
        return this.date;
    }

    public @NotNull MessageRecord setDate(@Nullable final String date) {
        this.date = date;
        return this;
    }

    public @Nullable String getTimeStamp() {
        return this.timeStamp;
    }

    public @NotNull MessageRecord setTimeStamp(@Nullable final String timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public @NotNull String getFromPlayerUuid() {
        return this.fromPlayerUuid;
    }

    public @NotNull MessageRecord setFromPlayerUuid(@Nullable final String fromPlayerUuid) {
        this.fromPlayerUuid = fromPlayerUuid;
        return this;
    }

    public @NotNull String getToPlayerUuid() {
        return this.toPlayerUuid;
    }

    public @NotNull MessageRecord setToPlayerUuid(@Nullable final String toPlayerUuid) {
        this.toPlayerUuid = toPlayerUuid;
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        final ConfigurationSection section = new MemoryConfigurationSection();

        section.set(UUID_FIELD, this.uuid);
        section.set(MESSAGE_FIELD, this.message);
        section.set(DATE_FIELD, this.date);
        section.set(TIMESTAMP_FIELD, this.timeStamp);
        section.set(FROM_PLAYER_UUID_FIELD, this.fromPlayerUuid);
        section.set(TO_PLAYER_UUID_FIELD, this.toPlayerUuid);

        return section;
    }

    @Override
    public @NotNull MessageRecord convert(@NotNull ConfigurationSection section) {

        this.uuid = Objects.requireNonNull(section.getString(UUID_FIELD));
        this.message = section.getString(MESSAGE_FIELD);
        this.date = section.getString(DATE_FIELD);
        this.timeStamp = section.getString(TIMESTAMP_FIELD);
        this.fromPlayerUuid = section.getString(FROM_PLAYER_UUID_FIELD);
        this.toPlayerUuid = section.getString(TO_PLAYER_UUID_FIELD);

        return this;
    }
}
