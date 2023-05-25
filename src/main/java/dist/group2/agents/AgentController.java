package dist.group2.agents;

import dist.group2.DiscoveryClient;
import dist.group2.NamingClient;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class AgentController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final SyncAgent syncAgent;

    @Autowired
    public AgentController(SyncAgent syncAgent) {
        this.syncAgent = syncAgent;
    }

    @PostMapping("/executeFailureAgent")
    public void executeFailureAgent(@RequestBody FailureAgent failureAgent) {
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

    public void startFailureAgent(int failingNodeId, int startingNodeId) {
        FailureAgent failureAgent = new FailureAgent(failingNodeId, startingNodeId);
        // Create a new thread for an agent
        Thread agentThread = new Thread(failureAgent);

        // Start the thread
        agentThread.start();

        // Wait for the thread to finish
        try {
            agentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Execute the REST method on the next node
        String nextNodeIP = NamingClient.getIPAddress(DiscoveryClient.getNextID());
        restTemplate.postForObject(nextNodeIP + "/executeFailureAgent", failureAgent, Void.class);
    }

    @GetMapping("/sync")
    public JSONArray listFiles() {
        return this.syncAgent.localFilesToSend();
    }
}
