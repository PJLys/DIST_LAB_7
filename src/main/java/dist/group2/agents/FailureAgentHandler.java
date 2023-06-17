package dist.group2.agents;

import dist.group2.DiscoveryClient;
import dist.group2.NamingClient;
import org.springframework.web.client.RestTemplate;

public class FailureAgentHandler implements Runnable {
    private final FailureAgent failureAgent;

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

        // Check if the agent needs to be terminated
        if (failureAgent.shouldTerminate(DiscoveryClient.getNextID())) {
            // The last node deletes the failing node from the database
            NamingClient.deleteNodeById(this.failureAgent.getFailingNodeId());
            return;
        }

        // Execute the REST method on the next node
        int nextNodeID = DiscoveryClient.getNextID();
        // Skip the failing node
        if (nextNodeID == this.failureAgent.getFailingNodeId()) {
            nextNodeID = NamingClient.getIdNextNode(nextNodeID);
        }
        String nextNodeIP = NamingClient.getIPAddress(nextNodeID);
        System.out.println("Sending failure agent to " + DiscoveryClient.getPreviousID() + " with IP " + nextNodeIP);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://" + nextNodeIP + ":8082/agents/executeFailureAgent", this.failureAgent, Void.class);
    }
}