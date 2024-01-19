package com.github.smuddgge.leaf.commands.subtypes.friends;

import com.github.smuddgge.leaf.FriendManager;
import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.dependencys.ProtocolizeDependency;
import com.github.smuddgge.leaf.inventorys.inventorys.FriendListInventory;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;
import com.velocitypowered.api.proxy.Player;

import java.util.List;
import java.util.UUID;

/**
 * <h1>Friend List Subcommand Type</h1>
 * Opens a {@link com.github.smuddgge.leaf.inventorys.InventoryInterface}
 * containing the players friends.
 */
public class FriendList implements CommandType {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getSyntax() {
        return "/[parent] [name]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        ConfigurationSection listSection = section.getSection(this.getName());

        // Check if the user is not allowed to see other players friend lists.
        if (listSection.getKeys().contains("permission_see_any")
                && !user.hasPermission(listSection.getString("permission_see_any"))) return null;

        // Check if the database is disabled.
        if (Leaf.isDatabaseDisabled()) return null;

        // Return all players in the database.
        return new CommandSuggestions().appendDatabasePlayers();
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();

        // Check if inventory interface is disabled.
        if (!ProtocolizeDependency.isInventoryEnabled()) {
            FriendList.sendMessage(user, section.getSection("list"), arguments);
            return new CommandStatus().error();
        }

        ConfigurationSection listSection = section.getSection(this.getName());

        // If a player is specified, and they have permission to see
        // other players friend lists.
        if (arguments.length >= 2
                && listSection.getKeys().contains("permission_see_any")
                && user.hasPermission(listSection.getString("permission_see_any"))) {

            // Check if the database is disabled.
            if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();
            PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);

            // Get the given argument.
            // This will be the owner of the friend list.
            String friendListOwnerName = arguments[1];

            // Get the players details.
            PlayerRecord playerRecord = playerTable.getFirstRecord(new Query().match("name", friendListOwnerName));

            // Check if the user exists in the database.
            if (playerRecord == null) {
                user.sendMessage(section.getString("not_found", "{error_colour}Player could not be found."));
                return new CommandStatus();
            }

            try {
                // Try to open the friend list inventory.
                new FriendListInventory(section.getSection(this.getName()), user, playerRecord.uuid).open();

            } catch (Exception exception) {
                user.sendMessage(listSection.getString("error", "{error_colour}Error occurred when opening inventory."));
                MessageManager.warn("Exception occurred when opening a another players friend list as a inventory!");
                exception.printStackTrace();
            }

            return new CommandStatus();
        }

        try {
            // Try to open the friend list inventory.
            new FriendListInventory(section.getSection(this.getName()), user).open();

        } catch (Exception exception) {
            user.sendMessage(listSection.getString("error", "{error_colour}Error occurred when opening inventory."));
            MessageManager.warn("Exception occurred when opening a friend list inventory normally!");
            exception.printStackTrace();
        }

        return new CommandStatus();
    }

    /**
     * Used to send the list of friends as a message.
     *
     * @param user The instance of the user running the command.
     * @param section The list section.
     * @param arguments The list of arguments.
     */
    public static void sendMessage(User user, ConfigurationSection section, String[] arguments) {
        ConfigurationSection messageSection = section.getSection("message");
        StringBuilder builder = new StringBuilder();

        // Get the header.
        if (messageSection.getKeys().contains("header")) {
            builder.append(section.getAdaptedString("header", "\n")).append("\n");
        }

        boolean canSeeOtherPlayers = section.getKeys().contains("permission_see_any")
                && user.hasPermission(section.getString("permission_see_any"))
                && !Leaf.isDatabaseDisabled();

        List<FriendRecord> friendRecords;

        if (canSeeOtherPlayers) {

            final String playerName = arguments[1];
            final Player player = Leaf.getServer().getPlayer(playerName).orElse(null);

            // Check if the player could not be found.
            if (player == null) {
                user.sendMessage(section.getString("not_found", "{error_colour}Player could not be found."));
                return;
            }

            // Load all the friend records.
            friendRecords = Leaf.getDatabase().getTable(FriendTable.class).getRecordList(
                    new Query().match("playerUuid", player.getUniqueId().toString())
            );
        } else {
            // Load all the friend records.
            friendRecords = Leaf.getDatabase().getTable(FriendTable.class).getRecordList(
                    new Query().match("playerUuid", user.getUniqueId().toString())
            );
        }

        assert friendRecords != null;
        for (FriendRecord friendRecord : friendRecords) {
            Player player = Leaf.getServer().getPlayer(UUID.fromString(friendRecord.friendPlayerUuid)).orElse(null);
            builder.append(
                    PlaceholderManager.parse(messageSection.getAdaptedString("name", "\n"), null, new User(player))
            ).append("\n");
        }

        // Get the footer.
        if (messageSection.getKeys().contains("footer")) {
            builder.append(section.getAdaptedString("footer", "\n"));
        }

        user.sendMessage(builder.toString());
    }
}
