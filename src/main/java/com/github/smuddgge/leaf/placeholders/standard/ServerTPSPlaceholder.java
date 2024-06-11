package com.github.smuddgge.leaf.placeholders.standard;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.StandardPlaceholder;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;

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
        return String.valueOf(SparkProvider.get().tps().poll(StatisticWindow.TicksPerSecond.SECONDS_5));
    }
}
