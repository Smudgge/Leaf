package com.github.smuddgge.leaf.datatype;

import com.github.smuddgge.leaf.utility.DateAndTime;

import java.util.*;

/**
 * Represents a query to get messages from the database.
 */
public class MessageQuery {

    public List<String> players = new ArrayList<>();
    public Long fromTimeStamp;
    public Long toTimeStamp;
    public List<String> include = new ArrayList<>();
    public List<String> exclude = new ArrayList<>();

    /**
     * Used to create a message query.
     *
     * <ul>
     *     <li>p:PlayerName,PlayerName</li>
     *     <li>t:AmountOfTime-AmountOfTime</li>
     *     <li>i:"include messages that contain this string","test"</li>
     *     <li>e:"exclude messages that contain this string","test"</li>
     * </ul>
     *
     * @param query A query as a string.
     */
    public MessageQuery(String query) {
        Map<String, String> map = MessageQuery.convertToMap(query);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (Objects.equals(entry.getKey(), "p")) {
                String[] players = entry.getValue().split(",");

                this.players = new ArrayList<>();
                this.players.addAll(new ArrayList<>(Arrays.asList(players)));
            }

            if (Objects.equals(entry.getKey(), "t")) {
                this.fromTimeStamp = DateAndTime.getFrom(entry.getValue());
                this.toTimeStamp = DateAndTime.getTo(entry.getValue());
            }

            if (Objects.equals(entry.getKey(), "i")) {
                this.include.addAll(Arrays.asList(entry.getValue().split(",")));
            }

            if (Objects.equals(entry.getKey(), "e")) {
                this.exclude.addAll(Arrays.asList(entry.getValue().split(",")));
            }
        }
    }

    /**
     * Used to convert a query to a map.
     *
     * <p>
     * Example query: p:Smudge,Smudge t:100d-100d i:"Test test test","oo oo oo"
     * Example conversion: {
     * "p": "Smudge,Smudge",
     * "t": "100d-100d",
     * "i": "Test test test,oo oo oo"
     * }
     * </p>
     *
     * @param query The query to convert.
     * @return The requested map.
     */
    private static Map<String, String> convertToMap(String query) {
        Map<String, String> map = new HashMap<>();

        boolean valueFlag = false;
        String type = "";
        StringBuilder value = new StringBuilder();

        // Loop though the query.
        for (String item : query.split(" ")) {
            // Check if the type has not ended
            // but the value has.
            if (valueFlag && item.contains("\",\"")) {
                String parsedItem = item.replace("\",\"", ",");
                value.append(parsedItem);
                continue;
            }

            // Check for the end of a value.
            if (valueFlag && item.contains("\"")) {
                valueFlag = false;
                value.append(item, 0, item.length() - 1);
                map.put(type, value.toString());
                continue;
            }

            // Check if it's parsing a value.
            if (valueFlag) {
                value.append(item);
                continue;
            }

            // Split into the type and value.
            String[] pair = item.split(":");
            if (pair.length < 2) continue;

            // If it contains a speech mark.
            if (pair[1].contains("\"")) {
                if (pair[1].endsWith("\"")) {
                    map.put(pair[0], pair[1].replace("\"", ""));
                    continue;
                }

                valueFlag = true;
                value = new StringBuilder(pair[1].substring(1));
                type = pair[0];
                continue;
            }

            map.put(pair[0], pair[1]);
        }

        return map;
    }
}
