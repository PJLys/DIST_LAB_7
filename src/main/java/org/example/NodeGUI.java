package org.example;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class NodeGUI extends JFrame {
    private JButton[] startButtons;
    private JButton[] stopButtons;
    private JTextArea fileInfoTextArea;

    public NodeGUI() {
        this.setTitle("Node GUI");
        this.setDefaultCloseOperation(3);
        this.setSize(600, 400);
        this.setLocationRelativeTo((Component)null);
        this.startButtons = new JButton[5];
        int i;
        for(i = 0; i < 5; ++i) {
            this.startButtons[i] = new JButton("Start Node " + (i + 1));
            int finalI = i;
            this.startButtons[i].addActionListener((e) -> {
                this.startNode(finalI + 1);
            });
        }

        this.stopButtons = new JButton[5];

        for(i = 0; i < 5; ++i) {
            this.stopButtons[i] = new JButton("Stop Node " + (i + 1));
            int finalI1 = i;
            this.stopButtons[i].addActionListener((e) -> {
                this.stopNode(finalI1 + 1);
            });
        }

        this.fileInfoTextArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(this.fileInfoTextArea);
        JPanel topPanel = new JPanel();

        for( i = 0; i < 5; ++i) {
            topPanel.add(this.startButtons[i]);
        }

        JPanel middlePanel = new JPanel();
        middlePanel.add(scrollPane);
        JPanel bottomPanel = new JPanel();

        for(i = 0; i < 5; ++i) {
            bottomPanel.add(this.stopButtons[i]);
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(topPanel, "North");
        mainPanel.add(middlePanel, "Center");
        mainPanel.add(bottomPanel, "South");
        this.add(mainPanel);
    }

    private void startNode(int nodeID) {
        JFrame extraFrame = new JFrame("Node Information");
        extraFrame.setSize(300, 200);
        extraFrame.setLocationRelativeTo((Component)null);
        String nodeIP = "192.168.0.1";
        String nodeStatus = "Running";
        String nodeName = "Node " + nodeID;
        NodeController.startNode(nodeID);
        JPanel nodeInfoPanel = new JPanel(new GridLayout(4, 1));
        nodeInfoPanel.add(new JLabel("Node ID: " + nodeID));
        nodeInfoPanel.add(new JLabel("Node IP: " + nodeIP));
        nodeInfoPanel.add(new JLabel("Node Status: " + nodeStatus));
        nodeInfoPanel.add(new JLabel("Node Name: " + nodeName));
        extraFrame.add(nodeInfoPanel);
        extraFrame.setVisible(true);
    }

    private void stopNode(int nodeID) {
        NodeController.stopNode(nodeID);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NodeGUI nodeGUI = new NodeGUI();
            nodeGUI.setVisible(true);
        });
    }
}
