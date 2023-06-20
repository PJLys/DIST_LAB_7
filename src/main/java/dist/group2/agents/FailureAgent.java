package dist.group2.agents;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import dist.group2.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@ToString
public class FailureAgent implements Serializable, Runnable{

    private final int failingNodeId;
    /**
     * List used to store all nodes that have been passed. It is used to check whether it passed all nodes in the ring
     */
    private final List<Integer> completedNodes;

    // Useless field, but it is needed to prevent errors during transmission of the agent. DON'T TRY TO REMOVE IT, YOU WILL LOSE A DAY DEBUGGING IT
    private final int startingNodeId;

    public FailureAgent(int failingNodeId) {
        this.failingNodeId = failingNodeId;
        this.startingNodeId = DiscoveryClient.getCurrentID();
        this.completedNodes = new ArrayList<>();
    }

    // Constructor for deserializing using Jackson ObjectMapper
    public FailureAgent(@JsonProperty("failingNodeId") int failingNodeId, @JsonProperty("completedNodes") List<Integer> completedNodes, @JsonProperty("startingNodeId") int startingNodeId) {
        this.failingNodeId = failingNodeId;
        this.completedNodes = completedNodes;
        this.startingNodeId = startingNodeId;
    }

    public List<Integer> getCompletedNodes() {
        return completedNodes;
    }

    public int getFailingNodeId() {
        return failingNodeId;
    }

    public int getStartingNodeId() {
        return completedNodes.get(0);
    }


    @Override
    public void run() {
        System.out.println("<FailureAgent> - Is running");
        // Check if the local files are owned by the failing node, and if so, send them to their new owner. The failing node does not have to do this.
        if (DiscoveryClient.getCurrentID() != this.failingNodeId) {
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
                    String newOwnerIP = NamingClient.getIPAddress(NamingClient.getIdPreviousNode(ownerID));
                    if (Client.checkIfOwner(newOwnerIP, file.getName())) {
                        // Add the current node to the replicated files
                        RestTemplate restTemplate = new RestTemplate();
                        // Check if the node is the owner himself
                        if (Objects.equals(newOwnerIP, DiscoveryClient.getIPAddress())) {
                            Logger.addReplicator(ReplicationClient.getLogFilePath().resolve(file.getName() + ".log").toString(), DiscoveryClient.getCurrentID());
                        }
                        else {
                            // Determine the request URL based on the IP address and filename
                            String requestUrl = "http://" + newOwnerIP + ":8082/api/" + file.getName() + "/" + DiscoveryClient.getCurrentID();
                            try {
                                // Send the HTTP request
                                ResponseEntity<Boolean> response = restTemplate.getForEntity(requestUrl, Boolean.class);
                            } catch (Exception e) {
                                // Handle any exceptions that may occur during the request
                                System.out.println("Failed to add this node to the replicator list of file " + file.getName() + " on node " + newOwnerIP);
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            ReplicationClient.sendFileToNode(file.getAbsolutePath(), null, newOwnerIP, "ENTRY_CREATE");
                        } catch (IOException e) {
                            System.out.println("Error occurred while sending file" + file.getName() + " to " + newOwnerIP + " by failure agent");
                            e.printStackTrace();
                        }
                    }
                }
            }

            // Update the nextID and previousID of the neighbouring nodes
            if (this.failingNodeId == DiscoveryClient.getPreviousID()) {
                System.out.println("\n<FailureAgent> - setting previousID of " +
                        DiscoveryClient.getCurrentID()+ " to "+
                        NamingClient.getIdPreviousNode(this.failingNodeId));

                DiscoveryClient.setPreviousID(NamingClient.getIdPreviousNode(this.failingNodeId));
            }
            if (this.failingNodeId == DiscoveryClient.getNextID()) {
                System.out.println("\n<FailureAgent> - setting nextID of "+
                        DiscoveryClient.getCurrentID()+ " to " +
                        NamingClient.getIdNextNode(this.failingNodeId));

                DiscoveryClient.setNextID(NamingClient.getIdNextNode(this.failingNodeId));
            }
        }
        // Add its own ID to the completedNodes
        this.completedNodes.add(DiscoveryClient.getCurrentID());
    }

    /**
     * If the failure agent has been executed by all nodes in the ring, it can be safely terminated.
     * @param nextNodeId The ID of the next node in the topology
     * @return True if it can be stopped, false otherwise
     */
    public Boolean shouldTerminate(int nextNodeId) {
        return this.completedNodes.contains(nextNodeId);
    }
}
