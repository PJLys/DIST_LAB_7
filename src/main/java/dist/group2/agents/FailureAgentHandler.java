package dist.group2.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dist.group2.DiscoveryClient;
import dist.group2.NamingClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class FailureAgentHandler implements Runnable {
    private FailureAgent failureAgent;

    public FailureAgentHandler(FailureAgent failureAgent) {
        this.failureAgent = failureAgent;
    }

    @Override
    public void run() {
        // Create a new thread from the received agent
        Thread agentThread = new Thread(this.failureAgent);

        // Start the thread
        agentThread.start();

        // Wait for the thread to finish
        try {
            agentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Execute the REST method on the next node, which is the previous one in the topology
        int nextNodeID = DiscoveryClient.getPreviousID();
        // Skip the failing node
        if (nextNodeID == this.failureAgent.getFailingNodeId()) {
            nextNodeID = NamingClient.getIdPreviousNode(nextNodeID);
        }
        // Check if the agent needs to be terminated
        if (this.failureAgent.shouldTerminate(nextNodeID)) {
            // The last node deletes the failing node from the database
            NamingClient.deleteNodeById(this.failureAgent.getFailingNodeId());
            return;
        }

        String nextNodeIP = NamingClient.getIPAddress(nextNodeID);
        System.out.println("Sending failure agent to " + DiscoveryClient.getPreviousID() + " with IP " + nextNodeIP);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String failureAgentString = objectMapper.writeValueAsString(this.failureAgent);
            FailureAgent failureAgent1 = objectMapper.readValue(failureAgentString, FailureAgent.class);
            System.out.println("Success to read failure agent string " + failureAgent1);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Void> response = restTemplate.postForObject("http://" + nextNodeIP + ":8082/agents/executeFailureAgent", failureAgentString, ResponseEntity.class);
            System.out.println("Response: " + response.getBody());
        } catch (JsonProcessingException e) {
            System.out.println("Failed to send failure agent");
            e.printStackTrace();
        }

    }
}
