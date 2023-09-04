package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.brand.BrandProxyPingListener;
import com.github.smuddgge.leaf.brand.HooksInitializer;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.Command;
import com.github.smuddgge.leaf.commands.CommandHandler;
import com.github.smuddgge.leaf.commands.subtypes.friends.Friend;
import com.github.smuddgge.leaf.commands.types.*;
import com.github.smuddgge.leaf.commands.types.messages.*;
import com.github.smuddgge.leaf.configuration.ConfigDatabase;
import com.github.smuddgge.leaf.configuration.ConfigurationManager;
import com.github.smuddgge.leaf.database.tables.*;
import com.github.smuddgge.leaf.datatype.ProxyServerInterface;
import com.github.smuddgge.leaf.dependencys.ProtocolizeDependency;
import com.github.smuddgge.leaf.inventorys.SlotManager;
import com.github.smuddgge.leaf.listeners.EventListener;
import com.github.smuddgge.leaf.placeholders.ConditionManager;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.placeholders.conditions.MatchCondition;
import com.github.smuddgge.leaf.placeholders.conditions.PermissionCondition;
import com.github.smuddgge.leaf.placeholders.standard.*;
import com.github.smuddgge.squishydatabase.DatabaseCredentials;
import com.github.smuddgge.squishydatabase.DatabaseFactory;
import com.github.smuddgge.squishydatabase.console.Console;
import com.github.smuddgge.squishydatabase.interfaces.Database;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

@Plugin(
        id = "leaf",
        name = "Leaf",
        version = "3.9.0",
        description = "A velocity utility plugin",
        authors = {"Smudge"}
)
public class Leaf {

    private static Leaf plugin;
    private static ProxyServer server;

    private static CommandHandler commandHandler;
    private static Database database;

    private final Metrics.Factory metricsFactory;

    @Inject
    public Leaf(ProxyServer server, @DataDirectory final Path folder, Metrics.Factory metricsFactory) {
        Leaf.server = server;
        Leaf.plugin = this;

        // Set up the configuration files.
        ConfigurationManager.initialise(folder.toFile());

        // Set up the database
        Leaf.setupDatabase(folder.toFile());

        // Set up b stats
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        // Set up b stats
        int pluginId = 17381;
        this.metricsFactory.make(this, pluginId);

        // Log header
        MessageManager.logHeader();

        // Register placeholders
        PlaceholderManager.register(new PlayerNamePlaceholder());
        PlaceholderManager.register(new PlayerPingPlaceholder());
        PlaceholderManager.register(new PlayerUuidPlaceholder());
        PlaceholderManager.register(new PlayerServerPlaceholder());
        PlaceholderManager.register(new PlayerVanishedPlaceholder());
        PlaceholderManager.register(new VersionPlaceholder());

        // Register placeholder conditions
        ConditionManager.register(new MatchCondition());
        ConditionManager.register(new PermissionCondition());

        // Reload configuration to load custom placeholders correctly.
        ConfigurationManager.reload();

        // Append all command types
        Leaf.commandHandler = new CommandHandler();

        Leaf.commandHandler.addType(new Alert());
        Leaf.commandHandler.addType(new AlertRaw());
        Leaf.commandHandler.addType(new AlertMessage());
        Leaf.commandHandler.addType(new Chat());
        Leaf.commandHandler.addType(new Find());
        Leaf.commandHandler.addType(new Friend());
        Leaf.commandHandler.addType(new History());
        Leaf.commandHandler.addType(new Info());
        Leaf.commandHandler.addType(new Inventory());
        Leaf.commandHandler.addType(new Join());
        Leaf.commandHandler.addType(new List());
        Leaf.commandHandler.addType(new Message());
        Leaf.commandHandler.addType(new Reload());
        Leaf.commandHandler.addType(new Reply());
        Leaf.commandHandler.addType(new Report());
        Leaf.commandHandler.addType(new Send());
        Leaf.commandHandler.addType(new Servers());
        Leaf.commandHandler.addType(new Teleport());
        Leaf.commandHandler.addType(new Ignore());
        Leaf.commandHandler.addType(new IgnoreList());
        Leaf.commandHandler.addType(new ToggleMessages());
        Leaf.commandHandler.addType(new ToggleSpy());
        Leaf.commandHandler.addType(new UnIgnore());
        Leaf.commandHandler.addType(new MessageHistory());
        Leaf.commandHandler.addType(new Variable());
        Leaf.commandHandler.addType(new Mute());
        Leaf.commandHandler.addType(new UnMute());

        Leaf.reloadCommands();

        // Load slot types
        SlotManager.setup();

        // Check for dependencies.
        if (!ProtocolizeDependency.isEnabled()) {
            MessageManager.log("&7[Dependencies] Could not find optional dependency &fProtocolize");
            MessageManager.log("&7[Dependencies] Inventories and sounds will be disabled.");
            MessageManager.log(ProtocolizeDependency.getDependencyMessage());
        }

        // Events.
        Leaf.getServer().getEventManager().register(this, new BrandProxyPingListener());

        // Initialize hooks.
        HooksInitializer.init();
    }

    @Subscribe
    public void onPlayerJoin(ServerConnectedEvent event) {
        EventListener.onPlayerJoin(event);
        EventListener.onPlayerJoinCustomEvent(event);
    }

    @Subscribe
    public void onPlayerLeave(DisconnectEvent event) {
        EventListener.onPlayerLeave(event);
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        EventListener.onPlayerChat(event);
    }

    /**
     * Used to get the instance of the plugin.
     *
     * @return The instance of the plugin.
     */
    public static Leaf getPlugin() {
        return Leaf.plugin;
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
    public static Database getDatabase() {
        return Leaf.database;
    }

    /**
     * Used to quickly check if the database is disabled.
     *
     * @return True if the database is disabled.
     */
    public static boolean isDatabaseDisabled() {
        if (Leaf.getDatabase() == null) return true;
        return Leaf.getDatabase().isDisabled();
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

        try {
            String type = ConfigDatabase.get().getString("type", "SQLITE");

            // Check if it's an SQLITE database.
            if (Objects.equals(type, "SQLITE")) {
                DatabaseFactory databaseFactory = DatabaseFactory.SQLITE;
                Leaf.database = databaseFactory.create(
                        DatabaseCredentials.SQLITE(folder.getAbsolutePath() + File.separator + "database.sqlite3")
                );
            }

            // Check if it's a MONGO database.
            if (Objects.equals(type, "MONGO")) {
                DatabaseFactory databaseFactory = DatabaseFactory.MONGO;
                Leaf.database = databaseFactory.create(
                        DatabaseCredentials.MONGO(
                                ConfigDatabase.get().getString("connection_string", "none"),
                                ConfigDatabase.get().getString("database_name", "Database")
                        )
                );
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            Console.warn("Connection String : " + ConfigDatabase.get().getString("connection_string"));
        }

        if (ConfigDatabase.isDebugMode()) {
            Leaf.database.setDebugMode(true);
        }

        if (Leaf.database.isDisabled()) {
            MessageManager.warn("[Database] Unable to load database.");
            return;
        }

        // Set up the tables
        Leaf.database.createTable(new PlayerTable());
        Leaf.database.createTable(new HistoryTable());
        Leaf.database.createTable(new FriendTable());
        Leaf.database.createTable(new FriendMailTable());
        Leaf.database.createTable(new FriendRequestTable());
        Leaf.database.createTable(new FriendSettingsTable());
        Leaf.database.createTable(new IgnoreTable());
        Leaf.database.createTable(new MessageTable());
        Leaf.database.createTable(new MuteTable());
    }

    /**
     * Used to reload the commands.
     */
    public static void reloadCommands() {
        Leaf.commandHandler.unregister();

        for (String identifier : ConfigurationManager.getCommands().getAllIdentifiers()) {
            String commandTypeString = ConfigurationManager.getCommands().getCommandType(identifier);
            if (commandTypeString == null) continue;

            BaseCommandType commandType = Leaf.commandHandler.getType(commandTypeString);

            if (commandType == null) {
                MessageManager.warn("Invalid command type for : " + identifier);
                continue;
            }

            Leaf.commandHandler.append(new Command(identifier, commandType));
        }

        Leaf.commandHandler.register();
    }
}
