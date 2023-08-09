package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.ProxyServerInterface;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;

import java.util.ArrayList;

/**
 * <h1>List Command Type</h1>
 * Used to show a list of online players.
 */
public class List extends BaseCommandType {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        ArrayList<String> possiblePermissions = new ArrayList<>();

        for (String key : section.getSection("list").getKeys()) {
            String permission = section.getSection("list").getSection(key).getString("permission");
            possiblePermissions.add(permission);
        }

        MessageManager.log(this.getFormatted(section, possiblePermissions, possiblePermissions));
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        ArrayList<String> possiblePermissions = new ArrayList<>();
        ArrayList<String> sendersPermissions = new ArrayList<>();

        for (String key : section.getSection("list").getKeys()) {
            String permission = section.getSection("list").getSection(key).getString("permission");

            possiblePermissions.add(permission);

            if (!user.hasPermission(permission)) continue;

            sendersPermissions.add(permission);
        }

        user.sendMessage(this.getFormatted(section, sendersPermissions, possiblePermissions));
        return new CommandStatus();
    }

    /**
     * Used to get the formatted message.
     *
     * @param section             The section of configuration the command is from.
     * @param permissions         The list of permissions the player has.
     * @param possiblePermissions The list of the commands possible permissions.
     * @return The formatted message.
     */
    private String getFormatted(ConfigurationSection section, java.util.List<String> permissions, java.util.List<String> possiblePermissions) {
        ProxyServerInterface proxyServerInterface = new ProxyServerInterface(Leaf.getServer());

        // Build the message.
        StringBuilder builder = new StringBuilder();

        String header = section.getString("header", null);
        if (header != null) {
            builder.append(header).append("\n");
        }

        // For each rank in the list.
        for (String key : section.getSection("list").getKeys()) {
            ConfigurationSection innerSection = section.getSection("list").getSection(key);

            // Get the permission.
            String permission = innerSection.getString("permission");
            if (permission == null) continue;

            // Get the filtered list of players.
            // If the players permissions include the permission add vanished players to the list.
            java.util.List<User> players = proxyServerInterface.getFilteredPlayers(
                    permission, possiblePermissions, permissions.contains(permission)
            );

            // Don't include the section if there are 0 players.
            if (players.size() == 0) continue;

            // Append the header
            builder.append("\n").append(innerSection.getString("header")
                    .replace("%amount%", String.valueOf(players.size())));

            // Append the players
            for (User user : players) {
                String userSection = innerSection.getString("section").replace("%player%", user.getName());
                builder.append("\n").append(PlaceholderManager.parse(userSection, null, user));
            }

            builder.append("\n");
        }

        String footer = section.getString("footer", null);
        if (footer != null) {
            builder.append("\n").append(footer);
        }

        return builder.toString();
    }
}
