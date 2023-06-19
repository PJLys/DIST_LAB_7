package dist.group2.agents;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public AgentController(ObjectMapper objectMapper) {
        this.syncAgent = SyncAgent.getAgent();
        this.objectMapper = objectMapper;
    }

    public static void startFailureAgent(int failingNodeId) {
        System.out.println("Starting failure agent with failingNodeId" + failingNodeId);
        FailureAgent failureAgent = new FailureAgent(failingNodeId);
        // Create a new thread for a FailureAgentHandler
        new Thread(new FailureAgentHandler(failureAgent)).start();
    }

    @PostMapping("/executeFailureAgent")
    public ResponseEntity<Void> executeFailureAgent(@RequestBody String failureAgentString) {
        System.out.println("Failure agent string: " + failureAgentString);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            FailureAgent failureAgent = objectMapper.readValue(failureAgentString, FailureAgent.class);
        }
        catch (Exception e) {
            System.out.println("Failed to convert received file to Failure Agent");
        }
//        System.out.println("Received failure agent with failingNodeId " + failureAgent.getFailingNodeId() + " and startingNodeId " + failureAgent.getStartingNodeId());
//        new Thread(new FailureAgentHandler(failureAgent)).start();
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
