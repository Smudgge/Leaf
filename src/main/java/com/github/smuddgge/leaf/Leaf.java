package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.commands.Command;
import com.github.smuddgge.leaf.commands.CommandHandler;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.types.*;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.configuration.ConfigDatabase;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.database.sqlite.SQLiteDatabase;
import com.github.smuddgge.leaf.database.tables.FriendMailTable;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.database.tables.HistoryTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.ProxyServerInterface;
import com.github.smuddgge.leaf.events.EventManager;
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
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.File;
import java.nio.file.Path;

@Plugin(
        id = "leaf",
        name = "Leaf",
        version = "1.2.4-DEV",
        description = "A velocity utility plugin",
        authors = {"Smudge"}
)
public class Leaf {

    private static ProxyServer server;

    private static CommandHandler commandHandler;
    private static SQLiteDatabase database;

    @Inject
    public void SmUtility(ProxyServer server, @DataDirectory final Path folder) {
        Leaf.server = server;

        // Set up the configuration files
        ConfigCommands.initialise(folder.toFile());
        ConfigMessages.initialise(folder.toFile());
        ConfigDatabase.initialise(folder.toFile());

        // Set up the database
        Leaf.setupDatabase(folder.toFile());
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
        Leaf.commandHandler.addType(new History());
        Leaf.commandHandler.addType(new Info());
        Leaf.commandHandler.addType(new List());
        Leaf.commandHandler.addType(new Message());
        Leaf.commandHandler.addType(new Reload());
        Leaf.commandHandler.addType(new Reply());
        Leaf.commandHandler.addType(new Report());
        Leaf.commandHandler.addType(new Send());
        Leaf.commandHandler.addType(new Servers());

        // Experimental
        Leaf.commandHandler.addType(new Friends());

        Leaf.reloadCommands();
    }

    @Subscribe
    public void onPlayerJoin(ServerPostConnectEvent event) {
        EventManager.onPlayerJoin(event);
    }

    @Subscribe
    public void onPlayerLeave(DisconnectEvent event) {
        EventManager.onPlayerLeave(event);
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
     * Used to get the proxy server interface.
     *
     * @return The proxy interface.
     */
    public static ProxyServerInterface getInterface() {
        return new ProxyServerInterface(Leaf.server);
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
     * Used to get the database.
     *
     * @return The instance of the database.
     */
    public static SQLiteDatabase getDatabase() {
        return Leaf.database;
    }

    /**
     * Used to set up the database.
     *
     * @param folder The plugin's folder.
     */
    public static void setupDatabase(File folder) {
        // Set up the database
        if (!ConfigDatabase.get().getBoolean("enabled", true)) {
            Leaf.database = null;
            return;
        }

        Leaf.database = new SQLiteDatabase(folder, "database");
        boolean successful = Leaf.database.setup();

        if (!successful) {
            MessageManager.warn("[Database] Unable to load database.");
            return;
        }

        // Set up the tables
        Leaf.database.createTable(new PlayerTable(Leaf.database));
        Leaf.database.createTable(new HistoryTable(Leaf.database));
        Leaf.database.createTable(new FriendTable(Leaf.database));
        Leaf.database.createTable(new FriendMailTable(Leaf.database));
    }

    /**
     * Used to reload the commands.
     */
    public static void reloadCommands() {
        Leaf.commandHandler.unregister();

        for (String identifier : ConfigCommands.get().getSection("commands").getKeys()) {
            String commandTypeName = ConfigCommands.get().getSection("commands").getSection(identifier).getString("type");
            BaseCommandType commandType = Leaf.commandHandler.getType(commandTypeName);

            if (commandType == null) {
                MessageManager.warn("Invalid command type for : " + identifier);
                continue;
            }

            Leaf.commandHandler.append(new Command(identifier, commandType));
        }

        Leaf.commandHandler.register();
    }
}
