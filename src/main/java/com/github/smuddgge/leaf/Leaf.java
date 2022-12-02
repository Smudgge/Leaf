package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.commands.Command;
import com.github.smuddgge.leaf.commands.CommandHandler;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.commands.types.*;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.placeholders.ConditionManager;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.placeholders.conditions.MatchCondition;
import com.github.smuddgge.leaf.placeholders.conditions.PermissionCondition;
import com.github.smuddgge.leaf.placeholders.standard.PlayerNamePlaceholder;
import com.github.smuddgge.leaf.placeholders.standard.ServerPlaceholder;
import com.github.smuddgge.leaf.placeholders.standard.VanishedPlaceholder;
import com.github.smuddgge.leaf.placeholders.standard.VersionPlaceholder;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.file.Path;

@Plugin(
        id = "leaf",
        name = "Leaf",
        version = "1.0.0",
        description = "A velocity utility plugin",
        authors = {"Smudge"}
)
public class Leaf {

    private static ProxyServer server;

    private static CommandHandler commandHandler;

    @Inject
    public void SmUtility(ProxyServer server, @DataDirectory final Path folder) {
        Leaf.server = server;

        // Setup configuration files
        ConfigCommands.initialise(folder.toFile());
        ConfigMessages.initialise(folder.toFile());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        // Log header
        MessageManager.logHeader();

        // Register placeholders
        PlaceholderManager.register(new PlayerNamePlaceholder());
        PlaceholderManager.register(new ServerPlaceholder());
        PlaceholderManager.register(new VanishedPlaceholder());
        PlaceholderManager.register(new VersionPlaceholder());

        // Register placeholder conditions
        ConditionManager.register(new MatchCondition());
        ConditionManager.register(new PermissionCondition());

        // Reload configuration to load custom placeholders
        ConfigMessages.reload();

        // Append all command types
        Leaf.commandHandler = new CommandHandler();

        Leaf.commandHandler.addType(new Alert());
        Leaf.commandHandler.addType(new AlertRaw());
        Leaf.commandHandler.addType(new Chat());
        Leaf.commandHandler.addType(new Find());
        Leaf.commandHandler.addType(new Info());
        Leaf.commandHandler.addType(new List());
        Leaf.commandHandler.addType(new Reload());
        Leaf.commandHandler.addType(new Report());
        Leaf.commandHandler.addType(new Send());
        Leaf.commandHandler.addType(new Servers());

        Leaf.reloadCommands();
    }

    /**
     * Used to get the proxy server instance.
     *
     * @return The proxy server.
     */
    public static ProxyServer getServer() {
        return Leaf.server;
    }

    /**
     * Used to get the command handler.
     *
     * @return The instance of the command handler.
     */
    public static CommandHandler getCommandHandler() {
        return Leaf.commandHandler;
    }

    /**
     * Used to reload the commands.
     */
    public static void reloadCommands() {
        Leaf.commandHandler.unregister();

        for (String identifier : ConfigCommands.get().getSection("commands").getKeys()) {
            String commandTypeName = ConfigCommands.get().getSection("commands").getSection(identifier).getString("type");
            CommandType commandType = Leaf.commandHandler.getType(commandTypeName);

            if (commandType == null) {
                MessageManager.warn("Invalid command type for : " + identifier);
                continue;
            }

            Leaf.commandHandler.append(new Command(identifier, commandType));
        }

        Leaf.commandHandler.register();
    }
}
