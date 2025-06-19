package org.main;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LLVMWindow extends JFrame {
    private JTextArea llvmTextArea;
    private List<String> llvmIRCode;

    public LLVMWindow(List<String> llvmIRCode) {
        this.llvmIRCode = llvmIRCode;
        initComponents();
        setTitle("LLVM IR");
        setPreferredSize(new Dimension(800, 600));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240)); // Light gray

        // Create Label for LLVM IR
        JLabel llvmLabel = new JLabel("LLVM IR");
        llvmLabel.setHorizontalAlignment(SwingConstants.CENTER);
        llvmLabel.setForeground(Color.BLACK);
        llvmLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        // Create Text Area for LLVM IR
        llvmTextArea = new JTextArea();
        llvmTextArea.setEditable(false);
        llvmTextArea.setBackground(Color.WHITE);
        llvmTextArea.setForeground(Color.BLACK);
        llvmTextArea.setFont(new Font("Consolas", Font.PLAIN, 16));

        // Build LLVM IR
        StringBuilder llvmIR = new StringBuilder();
        for (String line : llvmIRCode) {
            llvmIR.append(line).append("\n");
        }
        llvmTextArea.setText(llvmIR.toString());

        // Create Scroll Pane for LLVM IR
        JScrollPane llvmScrollPane = new JScrollPane(llvmTextArea);
        llvmScrollPane.setBorder(null);
        llvmScrollPane.setBackground(new Color(240, 240, 240)); // Light gray
        llvmScrollPane.getViewport().setBackground(new Color(240, 240, 240)); // Light gray

        // Add components to panel
        mainPanel.add(llvmLabel, BorderLayout.NORTH);
        mainPanel.add(llvmScrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }
} 