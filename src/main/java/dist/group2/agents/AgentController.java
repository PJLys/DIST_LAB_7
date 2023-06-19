package dist.group2.agents;

import net.minidev.json.JSONArray;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

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
    public ResponseEntity<Void> executeFailureAgent(@RequestBody FailureAgent failureAgent) {
        System.out.println("Received failure agent with failingNodeId " + failureAgent.getFailingNodeId() + " and startingNodeId " + failureAgent.getStartingNodeId());
        new Thread(new FailureAgentHandler(failureAgent)).start();
        return ResponseEntity.noContent().build();
    }
//    @PostMapping("/executeFailureAgent")
//    public void executeFailureAgent(@RequestBody String test) {
////        System.out.println("Received failure agent with failingNodeId " + failureAgent.getFailingNodeId() + " and startingNodeId " + failureAgent.getStartingNodeId());
////        new Thread(new FailureAgentHandler(failureAgent)).start();
//        System.out.println(test + " received");
//    }

    @GetMapping("/sync")
    public JSONArray listFiles() {
        return this.syncAgent.localFilesToSend();
    }
}
