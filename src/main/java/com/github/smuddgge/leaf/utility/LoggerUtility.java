package com.github.smuddgge.leaf.utility;

import com.github.smuddgge.leaf.Leaf;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

public class LoggerUtility {

    public static @NotNull String getLastLines(int amount) {
        final StringBuilder stringBuilder = new StringBuilder();
        try (
                // Open the file.
                final FileInputStream fileInputStream = new FileInputStream(
                        Leaf.getFolder().toAbsolutePath().toString()
                                .replace("/plugins/leaf", "") + "/logs/latest.log");
                // Get the list of lines in the file.
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream))
        ) {
            final List<String> lineList = bufferedReader.lines().toList();

            // Append the last amount.
            int position = 0;
            for (final String line : lineList) {
                position++;

                if (lineList.size() - amount > position) continue;
                stringBuilder.append(line).append('\n');
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // Return the lines.
        return stringBuilder.toString();
    }
}
