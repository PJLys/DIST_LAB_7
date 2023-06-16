package dist.group2.agents;

import dist.group2.DiscoveryClient;
import dist.group2.NamingClient;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(
        path = "agents"
)
public class AgentController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final SyncAgent syncAgent;

    @Autowired
    public AgentController() {
        this.syncAgent = SyncAgent.getAgent();
    }

    @PostMapping("/executeFailureAgent")
    public void executeFailureAgent(@RequestBody FailureAgent failureAgent) {
        System.out.println("Received failure agent with failingNodeId " + failureAgent.getFailingNodeId() + " and startingNodeId " + failureAgent.getStartingNodeId());
        new Thread(new FailureAgentHandler(failureAgent)).start();
//        // Create a new thread from the received agent
//        Thread agentThread = new Thread(failureAgent);
//
//        // Start the thread
//        agentThread.start();
//
//        // Wait for the thread to finish
//        try {
//            agentThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Check if the agent needs to be terminated
//        if (failureAgent.shouldTerminate()) {
//            return;
//        }
//
//        // Execute the REST method on the next node
//        String nextNodeUrl = NamingClient.getIPAddress(DiscoveryClient.getNextID());
//        restTemplate.postForObject("http://" + nextNodeUrl + ":8082/agents/executeFailureAgent", failureAgent, Void.class);
    }

    public void startFailureAgent(int failingNodeId, int startingNodeId) {
        System.out.println("Starting failure agent with failingNodeId" + failingNodeId + " and startingNodeId" + startingNodeId);
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
        System.out.println("Sending failure agent to " + DiscoveryClient.getNextID() + " with IP " + nextNodeIP);
        restTemplate.postForObject("http://" + nextNodeIP + ":8082/agents/executeFailureAgent", failureAgent, Void.class);
    }

    @GetMapping("/sync")
    public JSONArray listFiles() {
        return this.syncAgent.localFilesToSend();
    }
}
