package org.main;

import javax.swing.*;
import java.awt.*;

public class TerminalWindow extends JFrame {
    private JTextArea terminalArea;
    private JPanel mainPanel;

    public TerminalWindow() {
        super("Terminal");
        initComponents();
        setPreferredSize(new Dimension(800, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240)); // Light gray

        // Create Terminal Label
        JLabel terminalLabel = new JLabel("Terminal");
        terminalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        terminalLabel.setForeground(Color.BLACK);
        terminalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create Terminal
        terminalArea = new JTextArea();
        terminalArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        terminalArea.setEditable(true);
        terminalArea.setLineWrap(true);
        terminalArea.setWrapStyleWord(true);
        terminalArea.setBackground(Color.WHITE);
        terminalArea.setForeground(Color.BLACK);

        // Create scroll pane for terminal
        JScrollPane terminalScrollPane = new JScrollPane(terminalArea);
        terminalScrollPane.setBorder(null);

        // Add components to terminal panel
        mainPanel.add(terminalLabel, BorderLayout.NORTH);
        mainPanel.add(terminalScrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    public JTextArea getTerminalArea() {
        return terminalArea;
    }
}