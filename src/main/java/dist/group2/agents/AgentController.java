//package dist.group2.agents;
//
//import dist.group2.DiscoveryClient;
//import dist.group2.NamingClient;
//import net.minidev.json.JSONArray;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//
//
//@RestController
//
//public class AgentController {
//
//    private RestTemplate restTemplate = new RestTemplate();
//    private final SyncAgent syncAgent = new SyncAgent();
//
//    @PostMapping("/execute-agent")
//    public void executeAgent(@RequestBody FailureAgent failureAgent) {
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
//        restTemplate.postForObject(nextNodeUrl + "/execute-agent", failureAgent, Void.class);
//    }
//
//    @GetMapping("/sync")
//    public JSONArray listFiles() {
//        return this.syncAgent.localFilesToSend();
//    }
//}
