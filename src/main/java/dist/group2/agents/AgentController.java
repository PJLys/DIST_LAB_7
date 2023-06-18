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
    }

    public static void startFailureAgent(int failingNodeId) {
        System.out.println("Starting failure agent with failingNodeId" + failingNodeId);
        FailureAgent failureAgent = new FailureAgent(failingNodeId);
        // Create a new thread for a FailureAgentHandler
        new Thread(new FailureAgentHandler(failureAgent)).start();
    }

    @GetMapping("/sync")
    public JSONArray listFiles() {
        return this.syncAgent.localFilesToSend();
    }
}
