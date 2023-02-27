package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.records.IgnoreRecord;
import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the player ignore table.
 */
public class IgnoreTable extends TableAdapter<IgnoreRecord> {

    @Override
    public @NotNull String getName() {
        return "Ignore";
    }
}
