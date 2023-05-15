package dist.group2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class NamingClient {
    private static final RestTemplate restTemplate = new RestTemplate();
    private static String name = null;
    private static String baseUrl = null;

    public static void setName(String name) {
        NamingClient.name = name;
    }

    public static void setBaseUrl(String baseUrl) {
        NamingClient.baseUrl = baseUrl;
    }

    public static void addNode(String nodeName, String IPAddress) {
        String url = baseUrl + "/nodes";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("nodeName", nodeName);
        requestBody.put("IPAddress", IPAddress);

        try {
            System.out.println("<" + name + "> - Add node with name " + nodeName + " and IP address " + IPAddress);
            restTemplate.postForObject(url, requestBody, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("A hash collision occurred for node " + nodeName);
        }
    }

    public static void deleteNode(String nodeName) {
        String url = baseUrl + "/nodes/" + nodeName;
        restTemplate.delete(url);
        System.out.println("<" + name + "> - Deleted node with name " + nodeName);
    }


    public static String findFile(String fileName) {
        String url = baseUrl + "/files/" + fileName;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String IPAddress = response.getBody();
            System.out.println("<" + name + "> - " + fileName + " is stored at IPAddress " + IPAddress);
            return IPAddress;
        } catch (Exception e) {
            throw new RuntimeException("There are no nodes in the database");
            // Failure
        }
    }

    public static String getIPAddress(int nodeID) {
        String url = baseUrl + "/nodes/" + nodeID + "/ip";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String IPAddress = response.getBody();
            System.out.println("<" + name + "> - Node with ID " + nodeID + " has IPAddress " + IPAddress);
            return IPAddress;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find IPAddress of node with ID " + nodeID);
        }
    }

    public static int findFileNodeID(String fileName) {
        String url = baseUrl + "/nodeID" + "?fileName=" + fileName;
        try {
            ResponseEntity<Integer> response = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, Integer.class);
            int nodeID = response.getBody();
            System.out.println("<" + name + "> - " + fileName + " is stored at nodeID " + nodeID);
            return nodeID;
        } catch (Exception e) {
            throw new RuntimeException("There are no nodes in the database");
        }
    }
}