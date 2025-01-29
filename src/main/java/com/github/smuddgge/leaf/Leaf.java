package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.command.BaseCommandType;
import com.github.smuddgge.leaf.command.Command;
import com.github.smuddgge.leaf.command.CommandHandler;
import com.github.smuddgge.leaf.command.type.Info;
import com.github.smuddgge.leaf.configuration.*;
import com.github.smuddgge.leaf.database.*;
import com.github.smuddgge.leaf.logger.Logger;
import com.github.smuddgge.leaf.logger.SquishyLoggerAdapter;
import com.github.smuddgge.leaf.placeholder.CustomPlaceholder;
import com.github.smuddgge.leaf.placeholder.Placeholder;
import com.github.smuddgge.leaf.placeholder.PlaceholderManager;
import com.github.smuddgge.leaf.placeholder.standard.*;
import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.database.Database;
import com.github.squishylib.database.DatabaseBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Plugin(
        id = "leaf",
        name = "Leaf",
        version = "6.0.0.dev.0",
        description = "A velocity utility plugin.",
        authors = {"Smudge"}
)
public class Leaf {

    private static Leaf instance;

    private final @NotNull ProxyServer proxyServer;
    private final @NotNull File folder;
    private final @NotNull Logger logger;
    private final @NotNull Metrics.Factory metricsFactory;

    private Config config;
    private DatabaseConfig databaseConfig;
    private MessagesConfig messagesConfig;
    private CommandDirectory commandsDirectory;
    private PlaceholderDirectory placeholdersDirectory;
    private EventDirectory eventDirectory;

    private Database database;
    private final PlaceholderManager placeholderManager;
    private CommandHandler commandHandler;

    @Inject
    public Leaf(@NotNull ProxyServer proxyServer, @DataDirectory final Path folder, ComponentLogger componentLogger, Metrics.@NotNull Factory metricsFactory) {

        // Assign the pointer to this instance.
        Leaf.instance = this;

        try {

            // Set up basics.
            this.proxyServer = proxyServer;
            this.folder = folder.toFile();
            this.logger = new Logger(componentLogger);
            this.metricsFactory = metricsFactory;

            // Init placeholder manager.
            this.placeholderManager = new PlaceholderManager();

        } catch (Exception exception) {
            throw new LeafException(exception, "Leaf",
                    "Failed to initialise the plugin.",
                    "This is a unexpected error, please report it to the developer."
            );
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        // Set up the methods debug logger.
        Logger tempLogger = this.logger.extend(" &b.onProxyInitialization() &7Leaf.java:105");

        // Set up config files and directories.
        tempLogger.debug("Setting up config files and directories.");
        this.setUpConfigurationAndDirectories();

        // Display the plugin's name in console.
        this.logHeader();

        // Set up b stats.
        tempLogger.debug("Setting up b-stats.");
        this.setupBStats();

        // Set up the database.
        this.setupDatabase();

        // Set up placeholders.
        this.setupPlaceholders();

        // Set up commands.
        this.setupCommands();
    }

    private void logHeader() {
        try {
            final String version = Leaf.class.getAnnotation(Plugin.class).version();

            final String condensed = "Starting Leaf V&b%s".formatted(version);

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
                    """.formatted(version);

            // Should we log the big header or the condensed version?
            if (this.getConfig().shouldLogHeader()) this.logger.info(message);
            else this.logger.info(condensed);

        } catch (Exception exception) {
            throw new LeafException(exception, "Leaf.logHeader()", "Failed to log the header message.", null);
        }
    }

    private void setupBStats() {
        try {
            this.metricsFactory.make(this, 17381);
            this.logger.optional(Logger.Opt.B_STATS, "[b-stats] Enabled");
        } catch (Exception exception) {
            throw new LeafException(exception, "Leaf.setupBStats()",
                    "Failed to initialise b stats.",
                    "Please attempt to restart your proxy server. If this error still occurs please report it to the developer."
            );
        }
    }

    private void setUpConfigurationAndDirectories() {

        // Set up the methods debug logger.
        Logger tempLogger = this.logger.extend(" &b.setUpConfigurationAndDirectories() &7Leaf.java:143");

        try {
            tempLogger.debug("Initializing&b config.yml");
            this.config = new Config(this.folder, "config.yml");
            this.config.setResourcePath("config.yml");
            this.config.addListener((section) -> {
                if (((Config) section).inDebugMode()) {
                    this.setDebugMode(true);
                    return;
                }
                this.setDebugMode(false);
            });
            this.config.load();

            tempLogger.debug("Initializing&b database.yml");
            this.databaseConfig = new DatabaseConfig(this.folder, "database.yml");
            this.databaseConfig.setResourcePath("database.yml");
            this.databaseConfig.load();

            tempLogger.debug("Initializing&b messages.yml");
            this.messagesConfig = new MessagesConfig(this.folder, "messages.yml");
            this.messagesConfig.setResourcePath("messages.yml");
            this.messagesConfig.load();

            tempLogger.debug("Initializing&b Command Directory");
            this.commandsDirectory = new CommandDirectory(new File(this.folder, "commands"));
            this.commandsDirectory.addResourcePath("commands.yml");
            this.commandsDirectory.load(false);

            tempLogger.debug("Initializing&b Placeholder Directory");
            this.placeholdersDirectory = new PlaceholderDirectory(new File(this.folder, "placeholders"));
            this.placeholdersDirectory.addResourcePath("placeholders.yml");
            this.placeholdersDirectory.load(false);

            tempLogger.debug("Initializing&b Event Directory");
            this.eventDirectory = new EventDirectory(new File(this.folder, "events"));
            this.eventDirectory.addResourcePath("events.yml");
            this.eventDirectory.load(false);

        } catch (Exception exception) {
            throw new LeafException(exception, "Leaf.setupConfigurationAndDirectories()", "Failed to initialise config files and directories.", null);
        }
    }

    private void setupDatabase() {

        // Set up the methods debug logger.
        Logger tempLogger = this.logger.extend(" &b.setupDatabase() &7Leaf.java:173");

        try {

            DatabaseBuilder builder = new DatabaseBuilder(this.databaseConfig);
            builder.setLogger(new SquishyLoggerAdapter(this.logger));
            builder.setDebugMode(this.inDebugMode());
            tempLogger.debug("Initialized the database builder");

            // The database class does their own logging.
            this.database = builder.create();
            this.database.connect();

            // Add tables.
            this.database.createTable(new CommandCooldownTable());
            this.database.createTable(new CommandLimitTable());
            this.database.createTable(new FriendTable());
            this.database.createTable(new HistoryTable());
            this.database.createTable(new IgnoreTable());
            this.database.createTable(new MessageTable());
            this.database.createTable(new MuteTable());
            this.database.createTable(new PlayerTable());

        } catch (Exception exception) {
            throw new LeafException(exception, "Leaf.setupDatabase()", "Failed to initialise the database.", null);
        }
    }

    private void setupPlaceholders() {

        this.placeholderManager.register(new PlayerPingPlaceholder());
        this.placeholderManager.register(new PlayerPlaceholder());
        this.placeholderManager.register(new PlayerServerPlaceholder());
        this.placeholderManager.register(new PlayerUuidPlaceholder());
        this.placeholderManager.register(new PlayerVanishedPlaceholder());

        this.placeholderManager.register(new DisplayTitlePlaceholder());
        this.placeholderManager.register(new DisplaySubTitlePlaceholder());

        this.placeholderManager.register(new LeafVersionPlaceholder());
        this.placeholderManager.register(new VelocityVersionPlaceholder());

        this.registerCustomPlaceholders();
    }

    public void registerCustomPlaceholders() {
        for (String key : this.placeholdersDirectory.getKeys()) {
            final CustomPlaceholder placeholder = new CustomPlaceholder(key);
            this.placeholderManager.register(placeholder);
        }
    }

    public void unregisterCustomPlaceholders() {
        List<Placeholder> registeredCustomPlaceholders = this.placeholderManager.getPlaceholders()
                .stream().filter(placeholder -> placeholder instanceof CustomPlaceholder)
                .toList();

        for (Placeholder placeholder : registeredCustomPlaceholders) {
            this.placeholderManager.unregister(placeholder);
        }
    }

    public void setupCommands() {

        // Add command types.
        this.commandHandler = new CommandHandler();
        this.commandHandler.addCommandType(new Info());

        // Reload commands.
        this.reloadCommands();
    }

    public void reloadCommands() {

        // Unregister the current registered commands.
        this.commandHandler.unregisterCommands();
        this.logger.optional(Logger.Opt.COMMAND_REGISTERED, "[Commands] Unregistered Commands");

        for (final String identifier : this.commandsDirectory.getKeys()) {

            // Get the command section.
            final ConfigurationSection section = this.commandsDirectory.getSection(identifier);

            // Get the type of command.
            String commandTypeString = section.getString("type");
            if (commandTypeString == null) {
                this.logger.warn(" [Commands] The command with identifier &f" + identifier + " &edoes not have a command &ftype&e.");
                this.logger.warn(" [Commands] For Example:");
                this.logger.warn(" [Commands] command_identifier:");
                this.logger.warn(" [Commands]     &ftype&e: \"info\"");
                this.logger.warn(" [Commands]     name: \"command_name\"");
                this.logger.warn(" [Commands]     message: \"The message the command will reply with.\"");
                continue;
            }

            // Get the base command type.
            BaseCommandType commandType = this.commandHandler.getCommandType(commandTypeString);

            // Check if the command type doesn't exist.
            if (commandType == null) {
                this.logger.warn("[Commands] &f" + commandTypeString + " &eis not a valid command type. command identifier: &f" + identifier + "&e.");
                continue;
            }

            // Create the command and register.
            Command command = new Command(identifier, commandType);
            this.commandHandler.addCommand(command);
        }

        // Register all the commands with the velocity proxy.
        this.commandHandler.registerCommands();
    }

    public @NotNull ProxyServer getProxyServer() {
        return this.proxyServer;
    }

    public @NotNull File getFolder() {
        return this.folder;
    }

    public @NotNull Logger getLogger() {
        return this.logger;
    }

    public @NotNull Config getConfig() {
        return this.config;
    }

    public @NotNull DatabaseConfig getDatabaseConfig() {
        return this.databaseConfig;
    }

    public @NotNull MessagesConfig getMessagesConfig() {
        return this.messagesConfig;
    }

    public @NotNull CommandDirectory getCommandsDirectory() {
        return this.commandsDirectory;
    }

    public @NotNull PlaceholderDirectory getPlaceholdersDirectory() {
        return this.placeholdersDirectory;
    }

    public @NotNull EventDirectory getEventDirectory() {
        return this.eventDirectory;
    }

    public @NotNull Database getDatabase() {
        return this.database;
    }

    public @NotNull PlaceholderManager getPlaceholderManager() {
        return this.placeholderManager;
    }

    public boolean inDebugMode() {
        return this.config.inDebugMode();
    }

    public @NotNull Leaf setDebugMode(boolean debugMode) {
        this.logger.setDebugMode(debugMode);
        return this;
    }

    public boolean isDatabaseEnabled() {
        return this.getDatabaseConfig().isEnabled();
    }

    public boolean isDatabaseDisabled() {
        return !this.isDatabaseEnabled();
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
