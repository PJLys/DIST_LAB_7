package dist.group2;

import dist.group2.agents.SyncAgent;
import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class Client {
    private static final Path owned_files = Path.of("" ,"src", "replicatd_files");
    private final SyncAgent syncAgent;
    public Client() {
        SyncAgent syncAgent = SyncAgent.getAgent();
        Thread syncThread = new Thread(syncAgent);
        this.syncAgent = syncAgent;
        syncThread.start();
    }

    public void readfile(String filename) throws RuntimeException {
        Optional<Boolean> lock = this.getLock(filename);
        if (lock.isPresent() && lock.get()) {
            throw new RuntimeException("File is currently being written to!");
        }

        // Request the file
        String ipaddr = NamingClient.findFile(filename);
        this.request(ipaddr, filename, false);
    }

    public void editfile(String filename) throws RuntimeException {
        Optional<Boolean> lock = this.getLock(filename);
        if (lock.isPresent()) {
            throw new RuntimeException("File is in use!");
        }

        String ipaddr = NamingClient.findFile(filename);
        this.request(ipaddr, filename, true);
    }


    /**
     * Request a file to the remote node
     * @param ipaddr ip address of the node
     * @param filename name of requested file
     * @param method the action we want to perform on the file
     */
    private void request(String ipaddr, String filename, boolean method) {
        // Create the request body
        JSONObject body = new JSONObject();
        body.put("node", NamingClient.getName());

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP entity with body and headers
        HttpEntity<String> requestEntity = new HttpEntity<>(body.toString(), headers);

        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Determine the request URL based on the IP address and filename
        String requestUrl = "http://" + ipaddr + "/" + filename + "/" + (method ? "w" : "r");

        try {
            // Send the HTTP request
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    requestEntity,
                    byte[].class
            );

            // Retrieve the response body
            byte[] responseBody = response.getBody();

            // Perform any desired operations with the response body
            // ...

        } catch (Exception e) {
            // Handle any exceptions that may occur during the request
            e.printStackTrace();
        }
    }

    /**
     * Handle an incoming file request from another node
     * @param nodename name of the requesting node
     * @param filename name of the file that
     * @param method action of the requester
     */
    protected byte[] incomingRequest(String nodename, String filename, boolean method) {
        Path path_to_file = Paths.get(String.valueOf(owned_files), filename);
        if (!Files.exists(path_to_file)) {
            System.out.println("File " + filename + " not found in owned files!");
            return new byte[0];
        }
        try {
            return Files.readAllBytes(path_to_file);
        } catch (IOException e) {
            System.out.println("Failed to access file!\n\n");
            System.out.println(e.getMessage()+"\n\n");
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return new byte[0];
    }


    /**
     * Check the local list for the lock of the file
     * @param filename name of file we request the lock from
     * @return Optional containing either a read (false) or write (true) lock
     */
    private Optional<Boolean> getLock(String filename) {
        return this.syncAgent.getLock(filename);
    }

    /**
     * Request the ID of a node
     * @param nodeIP the IP address of the node to request
     * @return the ID of the node
     */
    public static int getNodeIdForIp(String nodeIP) {
        String url = "http://" + nodeIP + "/client/nodeID";
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            int nodeID = Integer.parseInt(Objects.requireNonNull(response.getBody()));
            System.out.println("Node with IP " + nodeIP + ":" + 8082 + " has ID " + nodeID);
            return nodeID;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to find ID of node with IP " + nodeIP);
        }
    }

    /**
     * Checks if the node with IP address nodeIP owns the file with name fileName
     * @param nodeIP the IP address of the node
     * @param fileName the name of the file
     * @return true if the node owns the file, false otherwise
     */
    public static boolean checkIfOwner(String nodeIP, String fileName) {
        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Determine the request URL based on the IP address and filename
        String requestUrl = "http://" + nodeIP + ":" + 8082 + "/client/" + fileName + "/owner";

        try {
            // Send the HTTP request
            ResponseEntity<Boolean> response = restTemplate.getForEntity(requestUrl, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            // Handle any exceptions that may occur during the request
            e.printStackTrace();
        }
        return false;
    }
}
