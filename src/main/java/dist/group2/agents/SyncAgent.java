package dist.group2.agents;

import dist.group2.DiscoveryClient;
import dist.group2.NamingClient;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * This is a sync agent. Its responsibility is to synchronize the files owned by the node with the available
 * files in the network.
 * @implements Runnable agent will run in a separate thread
 * @implements Serializable the agent has to be transmitted over the network
 *
 * @attribute localFiles holds the names of files on the node and if they're accessible
*/
public class SyncAgent implements Runnable, Serializable {
    private Map<String, Optional<Boolean>> networkfiles;
    public SyncAgent(Map<String,Optional<Boolean>> networkfiles) {
        this.networkfiles = networkfiles;
    }

    /**
     * Check for files, and then yield the CPU
     */
    @Override
    public void run() {
        this.checkfiles();
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
    private void checkfiles() {
        // CREATE REQUEST
        String nextIP = NamingClient.getIPAddress(DiscoveryClient.getNextID());
        RestTemplate template = new RestTemplate();
        // SEND HTTP REQUEST
        ResponseEntity<JSONArray> response = template.exchange(nextIP+"/sync", HttpMethod.GET, null, JSONArray.class);
        JSONArray jsarr = response.getBody();

        if (jsarr==null) {
            return;
        }

        for (Object obj : jsarr) {
            if (!(obj instanceof JSONObject))
                continue;
            JSONObject jsobj = (JSONObject) obj;
            String name = (String) jsobj.get("name");
            String lockValue = (String) jsobj.get("lock");
            Optional<Boolean> lock;
            if (!Objects.equals(lockValue, "")){
                lock = Optional.of(Boolean.parseBoolean(lockValue));
            } else {
                lock = Optional.empty();
            }
            networkfiles.put(name, lock);
        }

        throw new NotYetImplementedException("Create HTTP requests!");
    }

    private void updateNode() {
        // Update the local node list
        throw new NotYetImplementedException("Create update function");
    }
}

