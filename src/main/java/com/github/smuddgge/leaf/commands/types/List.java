package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.ProxyServerInterface;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;

import java.util.ArrayList;

/**
 * Represents the list command type.
 */
public class List implements CommandType {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
    }

    @Override
    public CommandSuggestions getSuggestions(User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        ArrayList<String> possiblePermissions = new ArrayList<>();


        for (String key : section.getSection("list").getKeys()) {
            String permission = section.getSection("list").getSection(key).getString("permission");
            possiblePermissions.add(permission);
        }

        String message = this.getFormatted(section, possiblePermissions, possiblePermissions);
        MessageManager.log(message);

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

        String message = this.getFormatted(section, sendersPermissions, possiblePermissions);

        user.sendMessage(message);

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

        StringBuilder builder = new StringBuilder();
        builder.append(section.getString("header")).append("\n");

        for (String key : section.getSection("list").getKeys()) {
            ConfigurationSection innerSection = section.getSection("list").getSection(key);

            String permission = innerSection.getString("permission");
            if (permission == null) continue;

            // Get the filtered list of players.
            // If permissions include permission include vanished players in the list.
            java.util.List<User> players = proxyServerInterface.getFilteredPlayers(permission, possiblePermissions, permissions.contains(permission));

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

        builder.append("\n").append(section.getString("footer"));

        return builder.toString();
    }
}
