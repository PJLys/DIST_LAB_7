package dist.group2.agents;

import dist.group2.DiscoveryClient;
import dist.group2.NamingClient;
import org.springframework.web.bind.annotation.RequestBody;
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
        if (failureAgent.shouldTerminate()) {
            return;
        }

        // Execute the REST method on the next node
        String nextNodeUrl = NamingClient.getIPAddress(DiscoveryClient.getNextID());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://" + nextNodeUrl + ":8082/agents/executeFailureAgent", failureAgent, Void.class);
    }
}
