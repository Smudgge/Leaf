package com.github.smuddgge.leaf.database;

import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.database.Record;
import com.github.squishylib.database.annotation.Field;
import com.github.squishylib.database.annotation.Primary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class PlayerRecord implements Record<PlayerRecord> {

    public static final @NotNull String UUID_FIELD = "uuid";
    public static final @NotNull String NAME_FIELD = "name";
    public static final @NotNull String TOGGLE_CAN_MESSAGE_FIELD = "toggleCanMessage";
    public static final @NotNull String TOGGLE_SEE_SPY_FIELD = "toggleSeeSpy";

    @Primary
    @Field(UUID_FIELD)
    public @NotNull String uuid;

    public @Field(NAME_FIELD) String name;
    public @Field(TOGGLE_CAN_MESSAGE_FIELD) String toggleCanMessage = null;
    public @Field(TOGGLE_SEE_SPY_FIELD) String toggleSeeSpy = null;

    public PlayerRecord(final @NotNull String uuid) {
        this.uuid = uuid;
    }

    public @NotNull UUID getUuid() {
        return UUID.fromString(uuid);
    }

    public @Nullable String getName() {
        return this.name;
    }

    public @NotNull PlayerRecord setName(@Nullable final String name) {
        this.name = name;
        return this;
    }

    public @Nullable String getToggleCanMessage() {
        return this.toggleCanMessage;
    }

    public @NotNull PlayerRecord setToggleCanMessage(@Nullable final String toggleCanMessage) {
        this.toggleCanMessage = toggleCanMessage;
        return this;
    }

    public @Nullable String getToggleSeeSpy() {
        return this.toggleSeeSpy;
    }

    public @NotNull PlayerRecord setToggleSeeSpy(@Nullable final String toggleSeeSpy) {
        this.toggleSeeSpy = toggleSeeSpy;
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        final ConfigurationSection section = new MemoryConfigurationSection();

        section.set(UUID_FIELD, this.uuid);
        section.set(NAME_FIELD, this.name);
        section.set(TOGGLE_CAN_MESSAGE_FIELD, this.toggleCanMessage);
        section.set(TOGGLE_SEE_SPY_FIELD, this.toggleSeeSpy);

        return section;
    }

    @Override
    public @NotNull PlayerRecord convert(@NotNull ConfigurationSection section) {

        this.uuid = Objects.requireNonNull(section.getString(UUID_FIELD));
        this.name = section.getString(NAME_FIELD);
        this.toggleCanMessage = section.getString(TOGGLE_CAN_MESSAGE_FIELD);
        this.toggleSeeSpy = section.getString(TOGGLE_SEE_SPY_FIELD);

        return this;
    }
}
