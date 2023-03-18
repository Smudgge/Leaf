package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>Represents the variable command type</h1>
 * Used to set a variable for a player.
 */
public class Variable extends BaseCommandType {

    @Override
    public String getName() {
        return "variable";
    }

    @Override
    public String getSyntax() {
        return "/[name] [value]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        List<String> suggestions = section.getListString("suggest", new ArrayList<>());
        return new CommandSuggestions().append(suggestions);
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();
        if (arguments.length < 1) return new CommandStatus().incorrectArguments();

        List<String> suggestions = section.getListString("suggest", new ArrayList<>());
        String value = String.join(" ", arguments);

        if (section.getBoolean("only_allow_suggestions", false)
                && !suggestions.contains(value)) {

            user.sendMessage(section.getString("incorrect_value", "{error_colour}You can only set the theme to &fgreen &7or &fgray."));
            return new CommandStatus();
        }

        PlayerRecord playerRecord = user.getRecord();

        // Current player variables.
        Map<String, Object> variables;

        // Attempt to get the players variables from the database.
        try {

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            variables = gson.fromJson(playerRecord.variables, type);

        } catch (Exception exception) {
            variables = new HashMap<>();
        }

        if (variables == null) variables = new HashMap<>();

        variables.put(section.getString("variable", "null"), value);

        playerRecord.variables = new Gson().toJson(variables);
        Leaf.getDatabase().getTable(PlayerTable.class).insertRecord(playerRecord);

        user.sendMessage(section.getString("correct_value", "{message} Theme is now set to &f<theme>"));
        return new CommandStatus();
    }
}
