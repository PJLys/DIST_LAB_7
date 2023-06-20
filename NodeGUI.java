package dist.group2;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeGUI extends JFrame {
    private JButton[] startButtons;
    private JButton[] stopButtons;
    private JTextArea fileInfoTextArea;

    public NodeGUI() {
        setTitle("Node GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Create start buttons
        startButtons = new JButton[5];
        for (int i = 0; i < 5; i++) {
            startButtons[i] = new JButton("Start Node " + (i + 1));
            int finalI = i;
            startButtons[i].addActionListener(e -> {
                try {
                    startNode(finalI + 1);
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }

        // Create stop buttons
        stopButtons = new JButton[5];
        for (int i = 0; i < 5; i++) {
            stopButtons[i] = new JButton("Stop Node " + (i + 1));
            int finalI = i;
            stopButtons[i].addActionListener(e -> stopNode(finalI + 1));
        }

        // Create scroll panel for file information
        fileInfoTextArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(fileInfoTextArea);

        // Create panels for start buttons, stop buttons, and scroll panel
        JPanel topPanel = new JPanel();
        for (int i = 0; i < 5; i++) {
            topPanel.add(startButtons[i]);
        }

        JPanel middlePanel = new JPanel();
        middlePanel.add(scrollPane);

        JPanel bottomPanel = new JPanel();
        for (int i = 0; i < 5; i++) {
            bottomPanel.add(stopButtons[i]);
        }

        // Create main panel and add components
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(middlePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add main panel to the frame
        add(mainPanel);
    }

    private void startNode(int nodeID) throws IOException {
        // Open extra frame and display node information
        JFrame extraFrame = new JFrame("Node Information");
        extraFrame.setSize(300, 200);
        extraFrame.setLocationRelativeTo(null);

        // Simulated node information
        String nodeIP = ;
        String nodeStatus = "Running";
        String nodeName = "Node " ;
        dist.group2.NodeController.startNode(nodeID);

        // Create node info panel and add components
        JPanel nodeInfoPanel = new JPanel(new GridLayout(4, 1));
        nodeInfoPanel.add(new JLabel("Node ID: " + ReplicationClient.getInstance().getNodeID()));
        nodeInfoPanel.add(new JLabel("Node IP: " + nodeIP));
        nodeInfoPanel.add(new JLabel("Node Status: " + nodeStatus));
        nodeInfoPanel.add(new JLabel("Node Name: " + nodeName));

        // Add node info panel to the extra frame
        extraFrame.add(nodeInfoPanel);
        extraFrame.setVisible(true);
    }

    private void stopNode(int nodeID) {
        dist.group2.NodeController.stopNode(nodeID);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NodeGUI nodeGUI = new NodeGUI();
            nodeGUI.setVisible(true);
        });
    }
}
