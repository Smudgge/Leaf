package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.datatype.ProxyServerInterface;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.discord.DiscordBotMessageAdapter;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <h1>Servers Command Type</h1>
 * Used to get the list of online servers
 * and the servers information.
 */
public class Servers extends BaseCommandType {

    @Override
    public String getName() {
        return "servers";
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
        // Log the message.
        MessageManager.log(this.getMessage(section));
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        // Send the message to the user.
        user.sendMessage(this.getMessage(section));
        return new CommandStatus();
    }

    @Override
    public CommandStatus onDiscordRun(ConfigurationSection section, SlashCommandInteractionEvent event) {
        // Get list placeholder.
        String list = this.getMessage(section.getSection("discord_bot"));

        // Create message.
        DiscordBotMessageAdapter message = new DiscordBotMessageAdapter(section, "discord_bot.message", "%list%")
                .setParser(new DiscordBotMessageAdapter.PlaceholderParser() {
                    @Override
                    public @NotNull String parsePlaceholders(@NotNull String string) {
                        return PlaceholderManager.parse(string, null, null)
                                .replace("%list%", list);
                    }
                });

        event.reply(message.buildMessage()).queue();
        return new CommandStatus();
    }

    /**
     * Used to get the server's message.
     *
     * @param section The instance of the configuration section.
     * @return The requested message.
     */
    private String getMessage(ConfigurationSection section) {
        ProxyServerInterface proxyServerInterface = new ProxyServerInterface(Leaf.getServer());

        // The message builder.
        StringBuilder builder = new StringBuilder();

        // Appear the header.
        String header = section.getAdaptedString("header", "\n", null);
        if (header != null) {
            builder.append(header).append("\n\n");
        }

        // Get the order of the servers.
        // This list also determines which servers will be displayed.
        List<String> order = section.getListString("order", new ArrayList<>());

        // For each server in the list.
        for (String serverName : order) {

            // Get the optional server.
            Optional<RegisteredServer> server = Leaf.getServer().getServer(serverName);

            // Check if the server exists.
            if (server.isEmpty()) continue;

            // Get the information message.
            String parsed = PlaceholderManager.parse(section.getAdaptedString("server", "\n"), null, new User(server.get(), null));

            // Get the number of players online.
            int online = proxyServerInterface.getFilteredPlayers(server.get(), null, false).size();

            // Add to the final message.
            builder.append(parsed.replace("%online%", String.valueOf(online)));
            builder.append("\n");
        }

        // Append the footer.
        String footer = section.getAdaptedString("footer", "\n", null);
        if (footer != null) {
            builder.append("\n");
            builder.append(section.getString("footer"));
        }

        return builder.toString();
    }
}

