package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.MuteTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;
import com.velocitypowered.api.proxy.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents the unmute command.
 * Used to unmute a player.
 */
public class UnMute extends BaseCommandType {

    @Override
    public String getName() {
        return "unmute";
    }

    @Override
    public String getSyntax() {
        return "/[name] [player]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return new CommandSuggestions().appendDatabasePlayers();
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();

        // Get the player's name.
        String playerName = arguments[0];

        // Get the player record.
        PlayerRecord record = Leaf.getDatabase().getTable(PlayerTable.class).getFirstRecord(new Query().match("name", playerName));
        if (record == null) {
            MessageManager.log(section.getAdaptedString("not_found", "\n", "&7Could not find player in the database."));
            return new CommandStatus();
        }

        // Get the player as a user.
        User playerToMute = new User(null, playerName);
        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(playerName);
        if (optionalPlayer.isPresent()) {
            playerToMute = new User(optionalPlayer.get());
        }

        // Unmute the player.
        Leaf.getDatabase().getTable(MuteTable.class).removeMute(UUID.fromString(record.uuid));

        // Log the mute.
        MessageManager.log(
                PlaceholderManager.parse(
                        section.getAdaptedString("unmute", "\n", "&7Unmuted <player>"),
                        null, playerToMute)
        );
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();

        // Get the player's name.
        String playerName = arguments[0];

        // Get the player record.
        PlayerRecord record = Leaf.getDatabase().getTable(PlayerTable.class).getFirstRecord(new Query().match("name", playerName));
        if (record == null) {
            MessageManager.log(section.getAdaptedString("not_found", "\n", "&7Could not find player in the database."));
            return new CommandStatus();
        }

        // Get the player as a user.
        User playerToMute = new User(null, playerName);
        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(playerName);
        if (optionalPlayer.isPresent()) {
            playerToMute = new User(optionalPlayer.get());
        }

        // Unmute the player.
        Leaf.getDatabase().getTable(MuteTable.class).removeMute(UUID.fromString(record.uuid));

        // Log the mute.
        user.sendMessage(
                PlaceholderManager.parse(
                        section.getAdaptedString("unmute", "\n", "&7Unmuted <player>"),
                        null, playerToMute)
        );
        return new CommandStatus();
    }
}
