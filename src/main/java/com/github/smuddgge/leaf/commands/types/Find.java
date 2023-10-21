package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.discord.DiscordBotMessageAdapter;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.velocitypowered.api.proxy.Player;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * <h1>Find Command Type</h1>
 * Used to get information on a online player.
 */
public class Find extends BaseCommandType {

    @Override
    public String getName() {
        return "find";
    }

    @Override
    public String getSyntax() {
        return "/[name] [player]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return new CommandSuggestions().appendPlayers(user);
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        // Get the player.
        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(arguments[0]);

        // Check if the player doesn't exist.
        if (optionalPlayer.isEmpty()) {
            String notFound = section.getAdaptedString("not_found", "\n");
            MessageManager.log(notFound);
            return new CommandStatus();
        }

        // Get the player as a user.
        User user = new User(optionalPlayer.get());

        // Log the result message.
        String found = section.getAdaptedString("found", "\n");
        MessageManager.log(PlaceholderManager.parse(found, null, user));

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        Optional<Player> optionalPlayer;

        if (arguments.length == 0) {

            // Get this player.
            optionalPlayer = Leaf.getServer().getPlayer(user.getUniqueId());
        } else {

            // Get the player.
            optionalPlayer = Leaf.getServer().getPlayer(arguments[0]);
        }

        // Check if the player doesn't exist.
        if (optionalPlayer.isEmpty()) {
            String notFound = section.getAdaptedString("not_found", "\n");
            user.sendMessage(notFound);
            return new CommandStatus();
        }

        // Get the player as a user.
        User foundUser = new User(optionalPlayer.get());

        // If vanishable players can find vanishable players, and the user is vanishable
        if (section.getBoolean("vanishable_players", false)
                && !user.isNotVanishable()) {

            // Send the result message to the player.
            String found = section.getAdaptedString("found", "\n");
            if (arguments.length == 0) found = section.getAdaptedString("found_no_args", "\n");
            user.sendMessage(PlaceholderManager.parse(found, null, foundUser));

            return new CommandStatus();
        }

        // Check if the user is vanished.
        if (foundUser.isVanished()) {
            String notFound = section.getAdaptedString("not_found", "\n");
            user.sendMessage(notFound);
            return new CommandStatus();
        }

        // Send the result message to the player.
        String found = section.getAdaptedString("found", "\n");
        if (arguments.length == 0) found = section.getAdaptedString("found_no_args", "\n");
        user.sendMessage(PlaceholderManager.parse(found, null, foundUser));

        return new CommandStatus();
    }

    @Override
    public void onDiscordRegister(ConfigurationSection section, @NotNull CommandCreateAction action) {
        action.addOption(OptionType.STRING, "player", "The players name.").complete();
    }

    @Override
    public CommandStatus onDiscordRun(ConfigurationSection section, SlashCommandInteractionEvent event) {
        OptionMapping mapping = event.getOption("player");

        // No player specified.
        if (mapping == null) {

            event.reply(new DiscordBotMessageAdapter(
                    section, "discord_bot.no_args", "Incorrect arguments."
            ).buildMessage()).queue();

            return new CommandStatus();
        }

        // Get the player.
        String playerName = mapping.getAsString();
        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(playerName);

        // Check if the player is offline.
        if (optionalPlayer.isEmpty() || new User(optionalPlayer.get()).isVanished()) {

            event.reply(new DiscordBotMessageAdapter(
                    section, "discord_bot.not_found", "Player not found."
            ).buildMessage()).queue();

            return new CommandStatus();
        }

        // Send the result message to the member.
        event.reply(new DiscordBotMessageAdapter(
                section, "discord_bot.found", "Player found on {server_formatted}"
        ).setParser(new User(optionalPlayer.get())).buildMessage()).queue();
        return new CommandStatus();
    }
}
