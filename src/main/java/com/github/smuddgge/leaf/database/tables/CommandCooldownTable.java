package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.records.CommandCooldownRecord;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandCooldownTable extends TableAdapter<CommandCooldownRecord> {

    @Override
    public @NotNull String getName() {
        return "CommandCooldown";
    }

    public long getExecutedTimeStamp(@NotNull UUID uniqueId, @NotNull String commandIdentifier) {
        CommandCooldownRecord record = this.getFirstRecord(new Query()
                .match("primaryKey", uniqueId.toString() + commandIdentifier)
        );
        if (record == null) return 0;
        return record.getLastExecutedTimeStamp();
    }

    public long getExecutedTimeStamp(@NotNull Member member, @NotNull String commandIdentifier) {
        CommandCooldownRecord record = this.getFirstRecord(new Query()
                .match("primaryKey", "Discord" + member.getUser().getName() + commandIdentifier)
        );
        if (record == null) return 0;
        return record.getLastExecutedTimeStamp();
    }

    public void updateExecutedTimeStamp(@NotNull UUID uniqueId, @NotNull String commandIdentifier) {
        CommandCooldownRecord record = this.getFirstRecord(new Query()
                .match("primaryKey", uniqueId.toString() + commandIdentifier)
        );

        if (record == null) {
            CommandCooldownRecord newRecord = new CommandCooldownRecord();
            newRecord.primaryKey = uniqueId.toString() + commandIdentifier;
            newRecord.setLastExecutedTimeStamp(System.currentTimeMillis());
            this.insertRecord(newRecord);
            return;
        }

        record.setLastExecutedTimeStamp(System.currentTimeMillis());
        this.insertRecord(record);
    }

    public void updateExecutedTimeStamp(@NotNull Member member, @NotNull String commandIdentifier) {
        CommandCooldownRecord record = this.getFirstRecord(new Query()
                .match("primaryKey", "Discord" + member.getUser().getName() + commandIdentifier)
        );

        if (record == null) {
            CommandCooldownRecord newRecord = new CommandCooldownRecord();
            newRecord.primaryKey = "Discord" + member.getUser().getName() + commandIdentifier;
            newRecord.setLastExecutedTimeStamp(System.currentTimeMillis());
            this.insertRecord(newRecord);
            return;
        }

        record.setLastExecutedTimeStamp(System.currentTimeMillis());
        this.insertRecord(record);
    }
}
