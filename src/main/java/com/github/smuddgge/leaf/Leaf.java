package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.brand.BrandProxyPingListener;
import com.github.smuddgge.leaf.brand.HooksInitializer;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.Command;
import com.github.smuddgge.leaf.commands.CommandHandler;
import com.github.smuddgge.leaf.commands.types.*;
import com.github.smuddgge.leaf.commands.types.friends.Friend;
import com.github.smuddgge.leaf.commands.types.messages.*;
import com.github.smuddgge.leaf.commands.types.whitelist.Whitelist;
import com.github.smuddgge.leaf.configuration.Config;
import com.github.smuddgge.leaf.configurationold.ConfigDatabase;
import com.github.smuddgge.leaf.configurationold.ConfigMain;
import com.github.smuddgge.leaf.configurationold.ConfigurationManager;
import com.github.smuddgge.leaf.configurationold.handlers.CommandConfigurationHandler;
import com.github.smuddgge.leaf.configurationold.handlers.EventConfigurationHandler;
import com.github.smuddgge.leaf.configurationold.handlers.PlaceholderConfigurationHandler;
import com.github.smuddgge.leaf.configurationold.handlers.VariableConfigurationHandler;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.*;
import com.github.smuddgge.leaf.datatype.ProxyServerInterface;
import com.github.smuddgge.leaf.dependencys.MiniPlaceholdersDependency;
import com.github.smuddgge.leaf.dependencys.ProtocolizeDependency;
import com.github.smuddgge.leaf.discord.DiscordBot;
import com.github.smuddgge.leaf.exception.LeafException;
import com.github.smuddgge.leaf.inventorys.SlotManager;
import com.github.smuddgge.leaf.listeners.EventListener;
import com.github.smuddgge.leaf.logger.Logger;
import com.github.smuddgge.leaf.placeholders.ConditionManager;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.placeholders.conditions.MatchCondition;
import com.github.smuddgge.leaf.placeholders.conditions.PermissionCondition;
import com.github.smuddgge.leaf.placeholders.standard.*;
import com.github.squishylib.configuration.Configuration;
import com.github.squishylib.configuration.implementation.YamlConfiguration;
import com.github.squishylib.database.Database;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

@Plugin(
        id = "leaf",
        name = "Leaf",
        version = "6.0.0.dev",
        description = "A velocity utility plugin.",
        authors = {"Smudge"}
)
public class Leaf {

    private static @NotNull Leaf instance;

    private ProxyServer proxyServer;
    private File folder;
    private Logger logger;
    private final Metrics.Factory metricsFactory;

    private Configuration config;
    private Configuration databaseConfig;
    private Configuration messagesConfig;
    private Configuration whitelistConfig;
    private CommandConfigurationHandler commandsDirectory;
    private PlaceholderConfigurationHandler placeholdersDirectory;
    private VariableConfigurationHandler variableDirectory;
    private EventConfigurationHandler eventDirectory;

    private CommandHandler commandHandler;
    private Database database;
    private DiscordBot discordBot;

    @Inject
    public Leaf(ProxyServer proxyServer, @DataDirectory final Path folder, ComponentLogger componentLogger, Metrics.Factory metricsFactory) {

        // Assign the pointer to this instance.
        Leaf.instance = this;

        try {

            // Set up basic variables.
            this.proxyServer = proxyServer;
            this.folder = folder.toFile();
            this.logger = new Logger(componentLogger);
            this.metricsFactory = metricsFactory;

        } catch (Exception exception) {
            throw new LeafException(exception, "Failed to initialise the plugin.");
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {

            // Set up the methods debug logger.
            Logger tempLogger = this.logger.extend(" &b.onProxyInitialization() &7Leaf.java:105");

            // Display the plugins name in console.
            this.logHeader();

            // Set up b stats.
            tempLogger.debug("Setting up b-stats.");
            this.metricsFactory.make(this, 17381);
            this.logger.info(" &7[b-stats] Enabled");

            // Set up configuration and directories.
            this.setUpConfigurationAndDirectories(tempLogger);

            // Set up the database.
            this.setUpDatabase(tempLogger);

            // Register placeholders.
            PlaceholderManager.register(new PlayerNamePlaceholder());
            PlaceholderManager.register(new PlayerPingPlaceholder());
            PlaceholderManager.register(new PlayerUuidPlaceholder());
            PlaceholderManager.register(new PlayerServerPlaceholder());
            PlaceholderManager.register(new PlayerVanishedPlaceholder());
            PlaceholderManager.register(new VersionPlaceholder());

            // Register placeholder conditions.
            ConditionManager.register(new MatchCondition());
            ConditionManager.register(new PermissionCondition());

            // Reload configuration to load custom placeholders correctly.
            ConfigurationManager.reload();

            // Append all command types.
            Leaf.commandHandler = new CommandHandler();

            Leaf.commandHandler.addType(new Alert());
            Leaf.commandHandler.addType(new AlertRaw());
            Leaf.commandHandler.addType(new AlertMessage());
            Leaf.commandHandler.addType(new Chat());
            Leaf.commandHandler.addType(new com.github.smuddgge.leaf.commands.types.Command());
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
            Leaf.commandHandler.addType(new Whitelist());

            Leaf.reloadCommands();

            // Load slot types.
            SlotManager.setup();

            // Check for dependencies.
            if (!ProtocolizeDependency.isEnabled()) {
                MessageManager.log("&7[Dependencies] Could not find optional dependency &fProtocolize");
                MessageManager.log("&7[Dependencies] Inventories and sounds will be disabled.");
                MessageManager.log(ProtocolizeDependency.getDependencyMessage());
            }

            if (!MiniPlaceholdersDependency.isEnabled()) {
                MessageManager.log("&7[Dependencies] Could not find optional dependency &fMini Placeholders");
                MessageManager.log("&7[Dependencies] Lets you use mini placeholders, not to be confused with leaf placeholders.");
                MessageManager.log(MiniPlaceholdersDependency.getDependencyMessage());
            }

            // Events.
            new BrandProxyPingListener().register(this, server.getEventManager());

            // Initialize hooks.
            HooksInitializer.init();

        } catch (Exception exception) {
            throw new LeafException(exception, "Failed to initialise the plugin.");
        }
    }

    private void logHeader() {
        final String message = """
                &7
                &a __         ______     ______     ______
                &a/\\ \\       /\\  ___\\   /\\  __ \\   /\\  ___\\
                &a\\ \\ \\____  \\ \\  __\\   \\ \\  __ \\  \\ \\  __\\
                &a \\ \\_____\\  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_\\
                &a  \\/_____/   \\/_____/   \\/_/\\/_/   \\/_/
                &7
                      &7By Smudge    Version &b%s
                &7
                &7| &aEnabled &7Discord Support &f~10mib
                &7
                """.formatted(Leaf.class.getAnnotation(Plugin.class).version());

        final String decreasedMessage = """
                &7
                &a __         ______     ______     ______
                &a/\\ \\       /\\  ___\\   /\\  __ \\   /\\  ___\\
                &a\\ \\ \\____  \\ \\  __\\   \\ \\  __ \\  \\ \\  __\\
                &a \\ \\_____\\  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_\\
                &a  \\/_____/   \\/_____/   \\/_/\\/_/   \\/_/
                &7
                      &7By Smudge    Version &b%s
                &7            
                """.formatted(Leaf.class.getAnnotation(Plugin.class).version());

        if (this.config.getBoolean(Config.DECREASED_LOGGING_HEADER_KEY)) {
            this.logger.info(decreasedMessage);
            return;
        }

        this.logger.info(message);
    }

    private void setUpConfigurationAndDirectories(@NotNull Logger tempLogger) {
        tempLogger.debug("Initializing&b config.yml");
        this.config = new YamlConfiguration(this.folder, "config.yml");
        this.config.setResourcePath("config.yml");
        this.config.load();

        tempLogger.debug("Initializing&b database.yml");
        this.databaseConfig = new YamlConfiguration(this.folder, "database.yml");
        this.databaseConfig.setResourcePath("database.yml");
        this.databaseConfig.load();

        tempLogger.debug("Initializing&b messages.yml");
        this.messagesConfig = new YamlConfiguration(this.folder, "messages.yml");
        this.messagesConfig.setResourcePath("messages.yml");
        this.messagesConfig.load();

        tempLogger.debug("Initializing&b whitelist.yml");
        this.whitelistConfig = new YamlConfiguration(this.folder, "whitelist.yml");
        this.whitelistConfig.setResourcePath("whitelist.yml");
        this.whitelistConfig.load();
    }

    private void setUpDatabase(@NotNull Logger tempLogger) {

    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        Leaf.discordBot.removeCommands().shutdown();
    }

    @Subscribe
    public void onPlayerSwitch(ServerConnectedEvent event) {
        EventListener.onPlayerSwitch(event);
        EventListener.onPlayerJoinCustomEvent(event);
    }

    @Subscribe
    public void onPlayerFirstJoin(PlayerChooseInitialServerEvent event) {

        // Check if the whitelist is enabled.
        if (Leaf.getWhitelistConfig().getBoolean("enabled")) {

            // Get the instance of the whitelist.
            java.util.List<String> whitelist = Leaf.getWhitelistConfig().getListString("players");
            final String name = event.getPlayer().getUsername();
            final String uuid = event.getPlayer().getUniqueId().toString();

            // Update name to uuid.
            if (whitelist.contains(name.toLowerCase())) {
                whitelist.remove(name.toLowerCase());
                whitelist.add(uuid);

                Leaf.getWhitelistConfig().set("players", whitelist);
                Leaf.getWhitelistConfig().save();
            }

            // Check if they are whitelisted.
            if (!whitelist.contains(uuid)) {
                event.getPlayer().disconnect(MessageManager.convertAndParse(
                        Leaf.getWhitelistConfig().getString("reason", "&fYou are not &cwhitelisted &fon this server."),
                        event.getPlayer()
                ));
                return;
            }
        }

        EventListener.onPlayerFirstJoin(event);
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
     * Used to get the instance of the folder.
     *
     * @return The instance of the folder.
     */
    public static Path getFolder() {
        return Leaf.folder;
    }

    /**
     * Used to get the instance of the component logger.
     *
     * @return The instance of the component logger.
     */
    public static ComponentLogger getComponentLogger() {
        return Leaf.componentLogger;
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
     * Used to get the instance of the whitelist configuration.
     *
     * @return The instance of the whitelist config.
     */
    public static @NotNull Configuration getWhitelistConfig() {
        return Leaf.whitelist;
    }

    /**
     * Get the whitelist as a list of player names.
     *
     * @return The list of player names on the whitelist.
     */
    public static @NotNull java.util.List<String> getWhitelist() {
        java.util.List<String> convertedList = new ArrayList<>();

        for (String playerEntry : Leaf.getWhitelistConfig().getListString("players")) {
            try {
                UUID uuid = UUID.fromString(playerEntry);

                // Check if they are online.
                if (Leaf.getServer().getPlayer(uuid).isPresent()) {
                    convertedList.add(Leaf.getServer().getPlayer(uuid).get().getUsername());
                    continue;
                }

                // Check the database.
                if (Leaf.getDatabase().isEnabled()) {
                    PlayerRecord record = Leaf.getDatabase().getTable(PlayerTable.class)
                            .getFirstRecord(new Query().match("uuid", uuid.toString()));
                    if (record == null) continue;
                    convertedList.add(record.name);
                }

            } catch (Exception exception) {
                convertedList.add(playerEntry);
            }
        }

        return convertedList;
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

        // Set up the database.
        if (!ConfigDatabase.get().getBoolean("enabled", true)) {
            Leaf.database = null;
            return;
        }

        // Create the database connection.
        try {
            Leaf.database = new DatabaseBuilder(
                    ConfigDatabase.get(),
                    folder.getAbsolutePath() + File.separator + "database.sqlite3"
            ).build();

        } catch (Exception exception) {
            Console.warn("Unable to create a connection to the database.");
            Console.warn("- If you are using mysql make sure the connection string is address:port");
            Console.warn("- Ensure you have filled the correct values in the database.yml config");
            Console.warn("&7");
            Console.warn("Connection String : " + ConfigDatabase.get().getString("connection_string"));
            Console.warn("&7");
            exception.printStackTrace();
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

        // Version 4.3.0
        Leaf.database.createTable(new CommandLimitTable());

        // Version 5.2.0
        Leaf.database.createTable(new CommandCooldownTable());
    }

    /**
     * Used to reload the commands.
     */
    public static void reloadCommands() {
        Leaf.commandHandler.unregister();

        if (!Leaf.commandHandler.isEmpty()) {
            MessageManager.warn("&7[Commands] &cUnregistered &7commands");
        }

        // Check if the discord bot is not null.
        if (Leaf.discordBot != null) {
            Leaf.discordBot.removeCommands().shutdown();
        }

        // Create new connection.
        Leaf.discordBot = new DiscordBot(ConfigMain.get().getString("discord_token", null));

        // Loop though commands.
        for (String identifier : ConfigurationManager.getCommands().getAllIdentifiers()) {

            // Get the type of command.
            String commandTypeString = ConfigurationManager.getCommands().getCommandType(identifier);
            if (commandTypeString == null) continue;

            // Get the base command type.
            BaseCommandType commandType = Leaf.commandHandler.getType(commandTypeString);

            // Check if the command type doesn't exist.
            if (commandType == null) {
                MessageManager.warn("Invalid command type for : " + identifier);
                continue;
            }

            // Create the command and register.
            Command command = new Command(identifier, commandType);
            Leaf.commandHandler.append(command);

            // Register the command with the discord bot.
            // This method will also check if it is a discord command.
            Leaf.discordBot.registerCommand(command);
        }

        Leaf.commandHandler.register();
    }

    /**
     * Gets the instance of the loaded leaf plugin.
     *
     * @return The instance of the leaf plugin.
     */
    public static @NotNull Leaf get() {
        return Leaf.instance;
    }
}
