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

public class IgnoreRecord implements Record<IgnoreRecord> {

    public static final @NotNull String UUID_FIELD = "uuid";
    public static final @NotNull String PLAYER_UUID_FIELD = "playerUuid";
    public static final @NotNull String IGNORED_PLAYER_UUID = "ignoredPlayerUuid";

    @Primary
    @Field(UUID_FIELD)
    public @NotNull String uuid;

    @Field(PLAYER_UUID_FIELD)
    @Foreign(table = "Player", tableField = "uuid")
    public String playerUuid;

    @Field(IGNORED_PLAYER_UUID)
    @Foreign(table = "Player", tableField = "uuid")
    public String ignoredPlayerUuid;

    public IgnoreRecord(final @NotNull String uuid) {
        this.uuid = uuid;
    }

    public @NotNull String getUuid() {
        return this.uuid;
    }

    public @Nullable UUID getPlayerUuid() {
        return UUID.fromString(this.playerUuid);
    }

    public @NotNull IgnoreRecord setPlayerUuid(@Nullable final UUID playerUuid) {
        this.playerUuid = playerUuid == null ? null : playerUuid.toString();
        return this;
    }

    public @Nullable UUID getIgnoredPlayerUuid() {
        return UUID.fromString(this.ignoredPlayerUuid);
    }

    public @NotNull IgnoreRecord setIgnoredPlayerUuid(@Nullable final UUID ignoredPlayerUuid) {
        this.ignoredPlayerUuid = ignoredPlayerUuid == null ? null : ignoredPlayerUuid.toString();
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        final ConfigurationSection section = new MemoryConfigurationSection();

        section.set(UUID_FIELD, getUuid());
        section.set(PLAYER_UUID_FIELD, getPlayerUuid());
        section.set(IGNORED_PLAYER_UUID, getIgnoredPlayerUuid());

        return section;
    }

    @Override
    public @NotNull IgnoreRecord convert(@NotNull ConfigurationSection section) {

        this.uuid = Objects.requireNonNull(section.getString(UUID_FIELD));
        this.playerUuid = section.getString(PLAYER_UUID_FIELD);
        this.ignoredPlayerUuid = section.getString(IGNORED_PLAYER_UUID);

        return this;
    }
}
