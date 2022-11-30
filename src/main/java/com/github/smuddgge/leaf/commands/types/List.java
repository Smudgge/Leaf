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
        java.util.List<String> permissions = new ArrayList<>();

        for (String key : section.getSection("list").getKeys()) {
            String permission = section.getSection("list").getSection(key).getString("permission");
            permissions.add(permission);
        }

        String message = this.getFormatted(section, permissions);
        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        java.util.List<String> permissions = new ArrayList<>();

        for (String key : section.getSection("list").getKeys()) {
            String permission = section.getSection("list").getSection(key).getString("permission");

            if (!user.hasPermission(permission)) continue;

            permissions.add(permission);
        }

        String message = this.getFormatted(section, permissions);

        user.sendMessage(message);

        return new CommandStatus();
    }

    private String getFormatted(ConfigurationSection section, java.util.List<String> permissions) {
        ProxyServerInterface proxyServerInterface = new ProxyServerInterface(Leaf.getServer());

        StringBuilder builder = new StringBuilder();
        builder.append(section.getString("header")).append("\n");

        for (String key : section.getSection("list").getKeys()) {
            ConfigurationSection innerSection = section.getSection("list").getSection(key);

            String permission = innerSection.getString("permission");
            if (permission == null) continue;

            // Get the filtered list of players.
            // If permissions include permission include vanished players in the list.
            java.util.List<User> players = proxyServerInterface.getFilteredPlayers(permission, permissions.contains(permission));

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
