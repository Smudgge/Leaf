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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Used to stop players running the message commands.
 */
public class Mute extends BaseCommandType {

    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public String getSyntax() {
        return "/[name] [player] <optional time>";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        List<String> timeList = new ArrayList<>();

        timeList.add("1m");
        timeList.add("1h");
        timeList.add("1d");

        return new CommandSuggestions().appendDatabasePlayers().append(timeList);
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

        long end = -1;
        String time = "forever";

        // Check if time is stated.
        if (arguments.length >= 2) {
            time = arguments[1];
            Duration duration = Duration.ofSeconds(1);

            // Parse.
            if (time.contains("m")) {
                duration = Duration.ofMinutes(Long.parseLong(time.replace("m", "")));
            }
            if (time.contains("h")) {
                duration = Duration.ofHours(Long.parseLong(time.replace("h", "")));
            }
            if (time.contains("d")) {
                duration = Duration.ofDays(Long.parseLong(time.replace("d", "")));
            }

            long milliseconds = duration.toMillis();
            end = System.currentTimeMillis() + milliseconds;
        }

        // Mute the player.
        Leaf.getDatabase().getTable(MuteTable.class).setMute(UUID.fromString(record.uuid), end);

        // Log the mute.
        MessageManager.log(
                PlaceholderManager.parse(
                                section.getAdaptedString("muted", "\n", "&7Muted <player> for &f%time%&7.")
                                , null, playerToMute)
                        .replace("%time%", time)
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

        long end = -1;
        String time = "forever";

        // Check if time is stated.
        if (arguments.length >= 2) {
            time = arguments[1];
            Duration duration = Duration.ofSeconds(1);

            // Parse.
            if (time.contains("m")) {
                duration = Duration.ofMinutes(Long.parseLong(time.replace("m", "")));
            }
            if (time.contains("h")) {
                duration = Duration.ofHours(Long.parseLong(time.replace("h", "")));
            }
            if (time.contains("d")) {
                duration = Duration.ofDays(Long.parseLong(time.replace("d", "")));
            }

            long milliseconds = duration.toMillis();
            end = System.currentTimeMillis() + milliseconds;
        }

        // Mute the player.
        Leaf.getDatabase().getTable(MuteTable.class).setMute(UUID.fromString(record.uuid), end);

        // Log the mute.
        user.sendMessage(
                PlaceholderManager.parse(
                                section.getAdaptedString("muted", "\n", "&7Muted <player> for &f%time%&7.")
                                , null, playerToMute)
                        .replace("%time%", time)
        );
        return new CommandStatus();
    }
}
