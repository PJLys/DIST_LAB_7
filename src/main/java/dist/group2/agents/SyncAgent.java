package dist.group2.agents;

import dist.group2.DiscoveryClient;
import dist.group2.NamingClient;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This is a sync agent. Its responsibility is to synchronize the files owned by the node with the available
 * files in the network.
 * @implements Runnable agent will run in a separate thread
 * @implements Serializable the agent has to be transmitted over the network
 *
 * @attribute localFiles holds the names of files on the node and if they're accessible
*/
@Component
@EnableScheduling
public class SyncAgent implements Runnable, Serializable {
    //Stores a filename with a lock:- If empty ==> No lock
    //                              - If present:  - true ==> No R/W
    //                                             - false ==> only R
    private final Map<String, Optional<Boolean>> networkfiles;
    private static SyncAgent instance;
    private SyncAgent() {
        this.networkfiles = new HashMap<>();
    }

    public static SyncAgent getAgent() {
        if (instance==null)
            instance = new SyncAgent();
        return instance;
    }

    /**
     * Check for files, and then yield the CPU
     */
    @Scheduled(fixedDelay =  500)   // Execute every 500 ms
    @Override
    public void run() {
        // Only run if it is not the only node in the system
        if (DiscoveryClient.getCurrentID() != DiscoveryClient.getNextID()) {
            this.updateNetworkFileStatus();
        }
        Thread.yield();
    }

    /**
     * Used to send the list of the agent over a rest api
     * @return JSON Formatted localFiles
     */
    public JSONArray localFilesToSend() {
        JSONArray jsonArray = new JSONArray();

        for (Map.Entry<String, Optional<Boolean>> entry : networkfiles.entrySet()) {
            String name = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
            String lock;

            if (entry.getValue().isPresent()) {
                lock = URLEncoder.encode(String.valueOf(entry.getValue().get()), StandardCharsets.UTF_8);
            } else {
                lock = ""; // or any default value you want to use when the Optional is empty
            }

            JSONObject jo = new JSONObject();
            jo.put("name", name);
            jo.put("lock", lock);
            jsonArray.add(jo);
        }
        return jsonArray;
    }


    /**
     * Ask for the information about the next node. Create HTTP request and receive information.
     */
    private void updateNetworkFileStatus() {
        System.out.println("--- SyncAgent is updating network file status ---");
        // CREATE REQUEST
        String nextIP = NamingClient.getIPAddress(DiscoveryClient.getNextID());
        RestTemplate template = new RestTemplate();
        // SEND HTTP REQUEST
        System.out.println("URL: " + "http://" + nextIP+":8082/sync");
        ResponseEntity<JSONArray> response = template.exchange("http://" + nextIP+":8082/agents/sync", HttpMethod.GET, null, JSONArray.class);
        int statusCode = response.getStatusCode().value();
        System.out.println("Response sync client status code:" + statusCode);
        if (statusCode != 200) {
            AgentController.startFailureAgent(DiscoveryClient.getNextID());
        }
        else {
            JSONArray jsarr = response.getBody();

            if (jsarr == null) {
                return;
            }
            // Update local network list
            for (Object obj : jsarr) {
                if (!(obj instanceof JSONObject jsobj))
                    continue;
                String name = (String) jsobj.get("name");
                String lockValue = (String) jsobj.get("lock");
                Optional<Boolean> lock;
                if (!Objects.equals(lockValue, "")) {
                    lock = Optional.of(Boolean.parseBoolean(lockValue));
                } else {
                    lock = Optional.empty();
                }
                networkfiles.put(name, lock);
            }
        }
    }

    /**
     * Return the status of the file to the client
     * @param filename name of requested file
     * @return empty if usable for R/W; false if usable for R; true if unusable
     */
    public Optional<Boolean> getLock(String filename){
        try {
            return this.networkfiles.get(filename);
        } catch (NullPointerException e){
            System.out.println("The Requested file: "+filename+ " is not present in the repository!\n" +
                    "Replying with 'true' since this results in the file not being usable\n\n");
            System.out.println(e.getMessage()+"\n\n");
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return Optional.of(true);
    }

    /**
     * Update local loc information based on the log of the filename
     * @param filename name of the file
     * @param method action that is to be performed on the file
     */
    public void handleLock(String filename, boolean method) {
        if (method) {
            // If the caller wants to write (boolean true), set a true flag to the file
            this.networkfiles.put(filename, Optional.of(true));
        } else {
            // If the caller wants to read (boolean false), set a false flag to the file
            this.networkfiles.put(filename, Optional.of(false));
        }
    }
}

