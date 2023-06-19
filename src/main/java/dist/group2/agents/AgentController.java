package dist.group2.agents;

import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        path = "agents"
)
public class AgentController {

    private final SyncAgent syncAgent;

    @Autowired
    public AgentController() {
        this.syncAgent = SyncAgent.getAgent();
    }

    public static void startFailureAgent(int failingNodeId) {
        System.out.println("Starting failure agent with failingNodeId" + failingNodeId);
        FailureAgent failureAgent = new FailureAgent(failingNodeId);
        // Create a new thread for a FailureAgentHandler
        new Thread(new FailureAgentHandler(failureAgent)).start();
    }

    @PostMapping("/executeFailureAgent")
    public void executeFailureAgent(@RequestBody FailureAgent failureAgent) {
        System.out.println("Received failure agent with failingNodeId " + failureAgent.getFailingNodeId() + " and startingNodeId " + failureAgent.getStartingNodeId());
        new Thread(new FailureAgentHandler(failureAgent)).start();
    }

    @GetMapping("/sync")
    public JSONArray listFiles() {
        return this.syncAgent.localFilesToSend();
    }
}
