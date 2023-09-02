package com.github.smuddgge.leaf.placeholders.standard;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.StandardPlaceholder;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.types.DoubleStatistic;

public class ServerTPSPlaceholder extends StandardPlaceholder {

    @Override
    public String getIdentifier() {
        return "tps";
    }

    @Override
    public String getValue(User user) {
        return this.getValue();
    }

    @Override
    public String getValue() {
        // Get spark provider.
        Spark spark = SparkProvider.get();

        // Get the TPS statistic (will be null on platforms that don't have ticks!)
        DoubleStatistic<StatisticWindow.TicksPerSecond> tps = spark.tps();
        if (tps == null) return null;

        // Retrieve the average TPS in the last 10 seconds / 5 minutes
        double tpsLast10Secs = tps.poll(StatisticWindow.TicksPerSecond.MINUTES_1);
        return String.valueOf(tpsLast10Secs);
    }
}
