package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.records.MuteRecord;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents the mute table.
 */
public class MuteTable extends TableAdapter<MuteRecord> {

    @Override
    public @NotNull String getName() {
        return "Mute";
    }

    /**
     * Used to get a mute record.
     * This may be null if they are not muted.
     *
     * @param uuid The uuid of the player.
     * @return The instance of the mute record.
     */
    public @Nullable MuteRecord getPlayer(@NotNull UUID uuid) {
        MuteRecord muteRecord = this.getFirstRecord(new Query()
                .match("uuid", uuid.toString())
        );

        // If the mute record doest exist.
        if (muteRecord == null) return null;

        long current = System.currentTimeMillis();
        long end = Long.parseLong(muteRecord.timeStampEnd);

        // If the mute lasts forever.
        if (end == -1) return muteRecord;

        // Check if the mute has ended.
        if (current >= end) {
            this.removeMute(uuid);
            return null;
        }

        return muteRecord;
    }

    /**
     * Used to set a players mute time.
     *
     * @param uuid         The uuid of the player.
     * @param endTimeStamp When the mute will end.
     */
    public void setMute(@NotNull UUID uuid, long endTimeStamp) {
        MuteRecord muteRecord = new MuteRecord();

        muteRecord.uuid = uuid.toString();
        muteRecord.timeStampCreate = String.valueOf(System.currentTimeMillis());
        muteRecord.timeStampEnd = String.valueOf(endTimeStamp);

        this.insertRecord(muteRecord);
    }

    /**
     * Used to remove a players mute.
     *
     * @param uuid The players uuid.
     */
    public void removeMute(@NotNull UUID uuid) {
        this.removeAllRecords(new Query()
                .match("uuid", uuid.toString())
        );
    }
}
