package dist.group2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NodeController {
    public static void startNode(int nodeID) {
        String command = getStartCommand(nodeID);
        if (command == null) {
            System.out.println("Invalid node ID.");
            return;
        }

        try {
            Process process = Runtime.getRuntime().exec(command);
            displayOutput(process);
            System.out.println("Node " + nodeID + " started successfully.");
        } catch (IOException e) {
            System.out.println("Error starting node: " + e.getMessage());
        }
    }

    public static void stopNode(int nodeID) {
        String command = getStopCommand(nodeID);
        if (command == null) {
            System.out.println("Invalid node ID.");
            return;
        }

        try {
            Process process = Runtime.getRuntime().exec(command);
            displayOutput(process);
            System.out.println("Node " + nodeID + " stopped successfully.");
        } catch (IOException e) {
            System.out.println("Error stopping node: " + e.getMessage());
        }
    }

    private static String getStartCommand(int nodeID) {
        switch (nodeID) {
            case 1:
                return "ssh -p 2021 root@6dist.idlab.uantwerpen.be bash start_node4.sh";
            case 2:
                return "ssh -p 2022 root@6dist.idlab.uantwerpen.be bash start_node3.sh";
            case 3:
                return "ssh -p 2023 root@6dist.idlab.uantwerpen.be bash start_node2.sh";
            case 4:
                return "ssh -p 2024 root@6dist.idlab.uantwerpen.be bash start_server.sh";
            case 5:
                return "ssh -p 2025 root@6dist.idlab.uantwerpen.be bash start_node1.sh";
            default:
                return null;
        }
    }

    private static String getStopCommand(int nodeID) {
        switch (nodeID) {
            case 1:
                return "ssh -p 2021 root@6dist.idlab.uantwerpen.be bash stop_node4.sh";
            case 2:
                return "ssh -p 2022 root@6dist.idlab.uantwerpen.be bash stop_node3.sh";
            case 3:
                return "ssh -p 2023 root@6dist.idlab.uantwerpen.be bash stop_node2.sh";
            case 4:
                return "ssh -p 2024 root@6dist.idlab.uantwerpen.be bash stop_server.sh";
            case 5:
                return "ssh -p 2025 root@6dist.idlab.uantwerpen.be bash stop_node1.sh";
            default:
                return null;
        }
    }

    public static void startServer() {
        try {
            Process process = Runtime.getRuntime().exec("ssh -p 2024 root@6dist.idlab.uantwerpen.be bash start_server.sh");
            displayOutput(process);
            System.out.println("Server started successfully.");
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    public static void stopServer() {
        try {
            Process process = Runtime.getRuntime().exec("ssh -p 2024 root@6dist.idlab.uantwerpen.be bash stop_server.sh");
            displayOutput(process);
            System.out.println("Server stopped successfully.");
        } catch (IOException e) {
            System.out.println("Error stopping server: " + e.getMessage());
        }
    }

    private static void displayOutput(Process process) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading command output: " + e.getMessage());
        }
    }
}
