package dist.group2.agents;
import dist.group2.Client;
import dist.group2.DiscoveryClient;
import dist.group2.NamingClient;
import dist.group2.ReplicationClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;


public class FailureAgent implements Runnable, Serializable {

    private final int failingNodeId;
    private final int startingNodeId;

    public FailureAgent(int failingNodeId, int startingNodeId) {
        this.failingNodeId = failingNodeId;
        this.startingNodeId = startingNodeId;
    }

    public int getFailingNodeId() {
        return failingNodeId;
    }

    public int getStartingNodeId() {
        return startingNodeId;
    }

    @Override
    public void run() {
        // Read the file list of the current node
        File localFolder = new File(ReplicationClient.getLocalFilePath().toUri());
        File[] localFiles = localFolder.listFiles();

        assert localFiles != null;
        for (File file : localFiles) {
            // Check if the failing node is the owner of the file
            int ownerID = NamingClient.findFileNodeID(file.getName());
            if (ownerID == failingNodeId) {
                System.out.println("Failing node is owner of file " + file.getName());
                // Check if the new owner already owns the file. Only send it if it does not own it yet
                String newOwnerIP = NamingClient.getIPAddressPreviousNode(ownerID);
                if (Client.checkIfOwner(newOwnerIP, file.getName())) {
                    // Add the current node to the replicated files
                    RestTemplate restTemplate = new RestTemplate();
                    // Determine the request URL based on the IP address and filename
                    String requestUrl = "http://" + newOwnerIP + "/api/" + file.getName() + "/" + DiscoveryClient.getCurrentID();
                    try {
                        // Send the HTTP request
                        ResponseEntity<Boolean> response = restTemplate.getForEntity(requestUrl, Boolean.class);
                    } catch (Exception e) {
                        // Handle any exceptions that may occur during the request
                        System.out.println("Failed to add this node to the replicator list of file " + file.getName() + " on node " + newOwnerIP);
                        e.printStackTrace();
                    }
                }
                else {
                    // Send the file and its log
                    String logPath = ReplicationClient.getLogFilePath().resolve(file.getName() + ".log").toString();
                    try {
                        ReplicationClient.sendFileToNode(file.getAbsolutePath(), logPath, newOwnerIP, "ENTRY_CREATE");
                    } catch (IOException e) {
                        System.out.println("Error occurred while sending file" + file.getName() + " to " + newOwnerIP + " by failure agent");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Boolean shouldTerminate() {
        return (DiscoveryClient.getCurrentID() == startingNodeId);
    }
}

