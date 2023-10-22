package com.github.smuddgge.leaf.utility;

import com.github.smuddgge.leaf.Leaf;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

public class LoggerUtility {

    public static @NotNull String getLastLines(int amount) {
        StringBuilder stringBuilder = new StringBuilder();

        try {

            // Open the file.
            FileInputStream fileInputStream = new FileInputStream(
                    Leaf.getFolder().toAbsolutePath().toString()
                            .replace("/plugins/leaf", "") + "/logs/latest.log"
            );

            // Get the list of lines in the file.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            List<String> lineList = bufferedReader.lines().toList();

            // Append the last amount.
            int position = 0;
            for (String line : lineList) {
                position++;

                if (lineList.size() - amount > position) continue;
                stringBuilder.append(line).append("\n");
            }

            // Close the input stream.
            fileInputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // Return the lines.
        return stringBuilder.toString();
    }
}
