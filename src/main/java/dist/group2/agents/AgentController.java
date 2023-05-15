package dist.group2.agents;

import dist.group2.DiscoveryClient;
import dist.group2.NamingClient;
import dist.group2.ReplicationClient;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@RestController
public class AgentController {

    private RestTemplate restTemplate = new RestTemplate();
    private final SyncAgent syncAgent = new SyncAgent();

    @PostMapping("/executeFailureAgent")
    public void executeAgent(@RequestBody FailureAgent failureAgent) {
        // Create a new thread from the received agent
        Thread agentThread = new Thread(failureAgent);

        // Start the thread
        agentThread.start();

        // Wait for the thread to finish
        try {
            agentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the agent needs to be terminated
        if (failureAgent.shouldTerminate()) {
            return;
        }

        // Execute the REST method on the next node
        String nextNodeUrl = NamingClient.getIPAddress(DiscoveryClient.getNextID());
        restTemplate.postForObject(nextNodeUrl + "/executeFailureAgent", failureAgent, Void.class);
    }

    @GetMapping("/files/existence")
    public boolean checkExistence(@RequestParam String fileName) {
        File localFile = new File(ReplicationClient.getLocalFilePath().toString() + "/" + fileName);
        File replicatedFile = new File(ReplicationClient.getReplicatedFilePath().toString() + "/" + fileName);
        return (localFile.exists() | replicatedFile.exists());
    }

    @GetMapping("/sync")
    public JSONArray listFiles() {
        return this.syncAgent.localFilesToSend();
    }
}
