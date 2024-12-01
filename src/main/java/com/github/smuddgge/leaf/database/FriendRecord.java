package com.github.smuddgge.leaf.database;

import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.database.Record;
import com.github.squishylib.database.annotation.Field;
import com.github.squishylib.database.annotation.Foreign;
import com.github.squishylib.database.annotation.Primary;
import org.jetbrains.annotations.NotNull;

import javax.print.DocFlavor;
import java.util.Objects;

public class FriendRecord implements Record<FriendRecord> {

    public static final @NotNull String UUID_FIELD = "uuid";
    public static final @NotNull String TIMESTAMP_CREATED_FIELD = "timestampCreated";
    public static final @NotNull String DATE_CREATED_FIELD = "dateCreated";
    public static final @NotNull String STARED_BOOLEAN = "staredBoolean";
    public static final @NotNull String FRIEND_NAME_FORMATTED = "friendNameFormatted";
    public static final @NotNull String FRIEND_NOTES = "friendNotes";
    public static final @NotNull String FRIEND_META = "friendMeta";
    public static final @NotNull String TOGGLE_PROXY_JOIN = "toggleProxyJoin";
    public static final @NotNull String TOGGLE_PROXY_LEAVE = "toggleProxyLeave";
    public static final @NotNull String TOGGLE_SERVER_CHANGE = "toggleServerChange";
    public static final @NotNull String PLAYER_UUID = "playerUuid";
    public static final @NotNull String FRIEND_PLAYER_UUID = "friendPlayerUuid";

    @Field(UUID_FIELD)
    @Primary
    private @NotNull String uuid;

    private @Field(TIMESTAMP_CREATED_FIELD) String timestampCreated;
    private @Field(DATE_CREATED_FIELD) String dateCreated;
    private @Field(STARED_BOOLEAN) String staredBoolean;
    private @Field(FRIEND_NAME_FORMATTED) String friendNameFormatted;
    private @Field(FRIEND_NOTES) String friendNotes;
    private @Field(FRIEND_META) String friendMeta;
    private @Field(TOGGLE_PROXY_JOIN) String toggleProxyJoin;
    private @Field(TOGGLE_PROXY_LEAVE) String toggleProxyLeave;
    private @Field(TOGGLE_SERVER_CHANGE) String toggleServerChange;

    @Field(PLAYER_UUID)
    @Foreign(table = "Player", tableField = "uuid")
    private String playerUuid;

    @Field(FRIEND_PLAYER_UUID)
    @Foreign(table = "Player", tableField = "uuid")
    private String friendPlayerUuid;

    public FriendRecord(@NotNull String uuid) {
        this.uuid = uuid;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        final ConfigurationSection section = new MemoryConfigurationSection();

        section.set(UUID_FIELD, uuid);
        section.set(TIMESTAMP_CREATED_FIELD, timestampCreated);
        section.set(DATE_CREATED_FIELD, dateCreated);
        section.set(STARED_BOOLEAN, staredBoolean);
        section.set(FRIEND_NAME_FORMATTED, friendNameFormatted);
        section.set(FRIEND_NOTES, friendNotes);
        section.set(FRIEND_META, friendMeta);
        section.set(TOGGLE_PROXY_JOIN, toggleProxyJoin);
        section.set(TOGGLE_PROXY_LEAVE, toggleProxyLeave);
        section.set(TOGGLE_SERVER_CHANGE, toggleServerChange);
        section.set(PLAYER_UUID, playerUuid);
        section.set(FRIEND_PLAYER_UUID, friendPlayerUuid);

        return section;
    }

    @Override
    public @NotNull FriendRecord convert(@NotNull ConfigurationSection section) {

        this.uuid = Objects.requireNonNull(section.getString(UUID_FIELD));
        this.timestampCreated = section.getString(TIMESTAMP_CREATED_FIELD);
        this.dateCreated = section.getString(DATE_CREATED_FIELD);
        this.staredBoolean = section.getString(STARED_BOOLEAN);
        this.friendNameFormatted = section.getString(FRIEND_NAME_FORMATTED);
        this.friendNotes = section.getString(FRIEND_NOTES);
        this.friendMeta = section.getString(FRIEND_META);
        this.toggleProxyJoin = section.getString(TOGGLE_PROXY_JOIN);
        this.toggleProxyLeave = section.getString(TOGGLE_PROXY_LEAVE);
        this.toggleServerChange = section.getString(TOGGLE_SERVER_CHANGE);
        this.playerUuid = section.getString(PLAYER_UUID);
        this.friendPlayerUuid = section.getString(FRIEND_PLAYER_UUID);

        return this;
    }
}
