package dist.group2;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    public static String getOwner(String filePath) {
        JSONObject jsonObject = readLogFile(filePath);
        return (String) jsonObject.get("owner");
    }

    public static List<String> getReplicators(String filePath) {
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
        System.out.println("Log file " + filePath + " created");
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
            return (JSONObject) JSONValue.parseWithException(json);
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println("Failed to read log file " + filePath);
        } catch (ParseException e) {
            System.out.println("Failed to parse log file " + filePath);
        }
        return new JSONObject();
    }

    public static void addReplicator(String filePath, String replicator) {
        JSONObject jsonObject = readLogFile(filePath);
        JSONArray replicators = (JSONArray) jsonObject.get("replicators");
        if (replicators == null) {
            System.out.println("Replicators not found or empty in log file " + filePath);
            replicators = new JSONArray();
        }
        replicators.add(replicator);
        writeJSONObject(filePath, jsonObject);
    }

    public static void removeReplicator(String filePath, String replicator) {
        JSONObject jsonObject = readLogFile(filePath);
        JSONArray replicators = (JSONArray) jsonObject.get("replicators");
        replicators.add(replicator);
        boolean success = replicators.remove(replicator);
        if (success) {
            System.out.println("Successfully removed " + replicator + " from log file " + filePath);
        }
        else {
            System.out.println(replicator + " not found in log file " + filePath);
        }
        writeJSONObject(filePath, jsonObject);
    }

    public static void setOwner(String filePath, String newOwner) {
        JSONObject jsonObject = readLogFile(filePath);
        jsonObject.put("owner", newOwner);
        System.out.println("Set " + newOwner + " as owner in log file " + filePath);
        writeJSONObject(filePath, jsonObject);
    }
}