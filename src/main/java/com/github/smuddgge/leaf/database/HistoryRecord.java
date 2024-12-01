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

public class HistoryRecord implements Record<HistoryRecord> {

    public static final @NotNull String UUID_FIELD = "uuid";
    public static final @NotNull String SERVER_FIELD = "server";
    public static final @NotNull String DATE_FIELD = "date";
    public static final @NotNull String TIMESTAMP_FIELD = "timeStamp";
    public static final @NotNull String EVENT_FIELD = "event";
    public static final @NotNull String PLAYER_UUID_FIELD = "playerUuid";


    @Primary
    @Field(UUID_FIELD)
    private @NotNull String uuid;

    private @Field(SERVER_FIELD) String server;
    private @Field(DATE_FIELD) String date;
    private @Field(TIMESTAMP_FIELD) String timeStamp;
    private @Field(EVENT_FIELD) String event;

    @Field(PLAYER_UUID_FIELD)
    @Foreign(table = "Player", tableField = "uuid")
    private String playerUuid;

    public HistoryRecord(final @NotNull String uuid) {
        this.uuid = uuid;
    }

    public @NotNull UUID getUuid() {
        return UUID.fromString(this.uuid);
    }

    public @Nullable String getServer() {
        return this.server;
    }

    public @NotNull HistoryRecord setServer(@Nullable final String server) {
        this.server = server;
        return this;
    }

    public @Nullable String getDate() {
        return this.date;
    }

    public @NotNull HistoryRecord setDate(@Nullable final String date) {
        this.date = date;
        return this;
    }

    public @Nullable String getTimeStamp() {
        return this.timeStamp;
    }

    public @NotNull HistoryRecord setTimeStamp(@Nullable final String timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public @Nullable String getEvent() {
        return this.event;
    }

    public @NotNull HistoryRecord setEvent(@Nullable final String event) {
        this.event = event;
        return this;
    }

    public @Nullable UUID getPlayerUuid() {
        return UUID.fromString(this.playerUuid);
    }

    public @NotNull HistoryRecord setPlayerUuid(@Nullable final UUID playerUuid) {
        this.playerUuid = playerUuid == null ? null : playerUuid.toString();
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        final ConfigurationSection section = new MemoryConfigurationSection();

        section.set(UUID_FIELD, this.uuid);
        section.set(SERVER_FIELD, this.server);
        section.set(DATE_FIELD, this.date);
        section.set(TIMESTAMP_FIELD, this.timeStamp);
        section.set(EVENT_FIELD, this.event);
        section.set(PLAYER_UUID_FIELD, this.playerUuid);

        return section;
    }

    @Override
    public @NotNull HistoryRecord convert(@NotNull ConfigurationSection section) {

        this.uuid = Objects.requireNonNull(section.getString(UUID_FIELD));
        this.server = section.getString(SERVER_FIELD);
        this.date = section.getString(DATE_FIELD);
        this.timeStamp = section.getString(TIMESTAMP_FIELD);
        this.event = section.getString(EVENT_FIELD);
        this.playerUuid = section.getString(PLAYER_UUID_FIELD);

        return this;
    }
}
