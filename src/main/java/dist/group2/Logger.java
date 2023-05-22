package dist.group2;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    private static String getOwner(String filePath) {
        JSONObject jsonObject = readLogFile(filePath);
        return (String) jsonObject.get("owner");
    }

    private static List<String> getReplicators(String filePath) {
        JSONObject jsonObject = readLogFile(filePath);
        JSONArray replicatorsArray = (JSONArray) jsonObject.get("replicators");
        List<String> replicatorsList = new ArrayList<>();
        for (Object replicator: replicatorsArray) {
            replicatorsList.add((String) replicator);
        }
        return replicatorsList;
    }

    public static void createLogFile(String filePath, String owner, List<String> replicators) {
        JSONObject logData = new JSONObject();
        JSONArray replicatorsArray = new JSONArray();
        replicatorsArray.addAll(replicators);
        logData.put("owner", owner);
        logData.put("replicators", replicatorsArray);
        writeJSONObject(filePath, logData);
    }

    private static void writeJSONObject(String filePath, JSONObject jsonObject) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonObject.toJSONString());
        } catch (IOException e) {
            System.out.println("Error while writing log file " + filePath);
            e.printStackTrace();
            ClientApplication.failure();
        }
    }
    private static JSONObject readLogFile(String filePath) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(filePath)));
            return (JSONObject) JSONValue.parse(json);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to read log file " + filePath);
            return new JSONObject();
        }
    }

    public static void addReplicator(String filePath, String replicator) {
        JSONObject jsonObject = readLogFile(filePath);
        JSONArray replicatedIPs = (JSONArray) jsonObject.get("replicatedIPs");
        replicatedIPs.add(replicator);
        writeJSONObject(filePath, jsonObject);
    }

    public static void removeReplicator(String filePath, String replicator) {
        JSONObject jsonObject = readLogFile(filePath);
        JSONArray replicatedIPs = (JSONArray) jsonObject.get("replicatedIPs");
        replicatedIPs.add(replicator);
        replicatedIPs.remove(replicator);
        writeJSONObject(filePath, jsonObject);
    }

    public static void setOwner(String filePath, String newOwner) {
        JSONObject jsonObject = readLogFile(filePath);
        jsonObject.put("owner", newOwner);
        writeJSONObject(filePath, jsonObject);
    }

}
