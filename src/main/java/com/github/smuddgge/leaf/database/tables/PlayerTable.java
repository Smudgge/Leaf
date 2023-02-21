package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents the player table in the database.
 */
public class PlayerTable extends TableAdapter<PlayerRecord> {

    @Override
    public @NotNull String getName() {
        return "Player";
    }

    /**
     * Used to update a user in the database.
     *
     * @param user User to update.
     */
    public void updatePlayer(User user) {
        PlayerRecord result = this.getFirstRecord(
                new Query().match("uuid", user.getUniqueId().toString())
        );

        // Check if the player record does not exist.
        if (result == null) {
            PlayerRecord playerRecord = new PlayerRecord();
            playerRecord.uuid = user.getUniqueId().toString();
            playerRecord.name = user.getName();

            this.insertRecord(playerRecord);
            return;
        }

        // Check if the player has changed their name.
        if (!Objects.equals(result.name, user.getName())) {
            result.name = user.getName();
            this.insertRecord(result);
        }
    }

    /**
     * Used to check if the player table contains a player name.
     *
     * @param playerName The players name to check for.
     * @return True if the player exists in the database.
     */
    public boolean contains(String playerName) {
        PlayerRecord playerRecord = this.getFirstRecord(new Query().match("name", playerName));
        return playerRecord != null;
    }
}
