package dist.group2.agents;
import dist.group2.DiscoveryClient;
import dist.group2.NamingClient;
import dist.group2.ReplicationClient;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;


public class FailureAgent implements Runnable, Serializable {

    private final int failingNodeId;
    private final int startingNodeId;

    public FailureAgent(int failingNodeId, int startingNodeId) {
        this.failingNodeId = failingNodeId;
        this.startingNodeId = startingNodeId;
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
            if (ownerID == DiscoveryClient.hashValue(String.valueOf(failingNodeId))) {
                // Check if the new owner already has the file
                String newOwnerIP = NamingClient.getIPAddressPreviousNode(ownerID);

                // Update and send the log file
                String logPath = ReplicationClient.getLogFilePath().resolve(file.getName() + ".log").toString();
                try {
                    FileWriter fileWriter = new FileWriter(logPath, true);
                    fileWriter.write("Owner changed from " + NamingClient.findFile(file.getName()) + " to " + newOwnerIP);
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("An error occurred while appending text to the log file of file " + file.getName() + " while executing FailureAgent");
                    e.printStackTrace();
                }


                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod
                // Change to the new sendFileMethod


//                if (!fileIsExisting(newOwnerIP, file.getName())) {
                    // Send the file to the new owner
//                     ReplicationClient.sendFile(file.getPath(), newOwnerIP);
//                }
//                 ReplicationClient.sendFileToNode(file.getPath(), newOwnerIP);
            }
        }
    }

    private boolean fileIsExisting(String nodeIP, String fileName) {
        String url = nodeIP +  "/files/existence?fileName=" + fileName;
        RestTemplate restTemplate = new RestTemplate();
        return Boolean.TRUE.equals(restTemplate.getForObject(url, Boolean.class));
    }

    public Boolean shouldTerminate() {
        return (DiscoveryClient.getCurrentID() == startingNodeId);
    }
}

