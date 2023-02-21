package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.records.FriendSettingsRecord;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FriendSettingsTable extends TableAdapter<FriendSettingsRecord> {

    @Override
    public @NotNull String getName() {
        return "FriendSettings";
    }

    /**
     * Used to get a players friend settings.
     * If the settings don't exist it will
     * create a new record.
     *
     * @param playerUuid The players uuid.
     * @return The instance of the friend settings.
     */
    public @NotNull FriendSettingsRecord getSettings(String playerUuid) {
        FriendSettingsRecord result = this.getFirstRecord(new Query().match("playerUuid", playerUuid));

        if (result == null) {
            FriendSettingsRecord settings = new FriendSettingsRecord();
            settings.uuid = UUID.randomUUID().toString();
            settings.playerUuid = playerUuid;

            this.insertRecord(settings);
            return settings;
        }

        return result;
    }
}
