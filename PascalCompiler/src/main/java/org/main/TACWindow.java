package org.main;

import IntermediateRepresentation.TACInstruction;
import IntermediateRepresentation.TACInstructionCollector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TACWindow extends JFrame {
    private JTable TAC;
    private JTable optimizedTACTable;
    private TACInstructionCollector tacInstructionCollector;
    private List<TACInstruction> optimizedTacCode;

    public TACWindow(TACInstructionCollector tacInstructionCollector, List<TACInstruction> optimizedTacCode) {
        this.tacInstructionCollector = tacInstructionCollector;
        this.optimizedTacCode = optimizedTacCode;
        initComponents();
        setTitle("Three Address Code");
        setPreferredSize(new Dimension(800, 600));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240)); // Light gray

        // Create TAC Panel
        JPanel tacPanel = new JPanel(new BorderLayout());
        tacPanel.setBackground(new Color(240, 240, 240)); // Light gray

        // Create Label for TAC
        JLabel tacLabel = new JLabel("TAC");
        tacLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tacLabel.setForeground(Color.BLACK);
        tacLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        // Create Scroll Pane for TAC
        TAC = new JTable();
        JScrollPane tacScrollPane = new JScrollPane(TAC);
        tacScrollPane.setBorder(null);
        tacScrollPane.setBackground(new Color(240, 240, 240)); // Light gray
        tacScrollPane.getViewport().setBackground(new Color(240, 240, 240)); // Light gray

        // Build TAC
        TAC.setModel(getTACTableModel(tacInstructionCollector.getInstructions()));
        TAC.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TAC.setBackground(Color.WHITE);
        TAC.setForeground(Color.BLACK);
        TAC.getTableHeader().setBackground(Color.LIGHT_GRAY);
        TAC.getTableHeader().setForeground(Color.BLACK);

        // Add components to TAC panel
        tacPanel.add(tacLabel, BorderLayout.NORTH);
        tacPanel.add(tacScrollPane, BorderLayout.CENTER);

        // Create Optimized TAC Panel
        JPanel optimizedTacPanel = new JPanel(new BorderLayout());
        optimizedTacPanel.setBackground(new Color(240, 240, 240)); // Light gray

        // Create Label for Optimized TAC
        JLabel optimizedTacLabel = new JLabel("TAC Optimizado");
        optimizedTacLabel.setHorizontalAlignment(SwingConstants.CENTER);
        optimizedTacLabel.setForeground(Color.BLACK);
        optimizedTacLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        // Create Scroll Pane for Optimized TAC
        optimizedTACTable = new JTable();
        JScrollPane optimizedTacScrollPane = new JScrollPane(optimizedTACTable);
        optimizedTacScrollPane.setBorder(null);
        optimizedTacScrollPane.setBackground(new Color(240, 240, 240)); // Light gray
        optimizedTacScrollPane.getViewport().setBackground(new Color(240, 240, 240)); // Light gray

        // Build Optimized TAC
        optimizedTACTable.setModel(getTACTableModel(optimizedTacCode));
        optimizedTACTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        optimizedTACTable.setBackground(Color.WHITE);
        optimizedTACTable.setForeground(Color.BLACK);
        optimizedTACTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        optimizedTACTable.getTableHeader().setForeground(Color.BLACK);

        // Add components to Optimized TAC panel
        optimizedTacPanel.add(optimizedTacLabel, BorderLayout.NORTH);
        optimizedTacPanel.add(optimizedTacScrollPane, BorderLayout.CENTER);

        // Add panels to main panel
        mainPanel.add(tacPanel);
        mainPanel.add(optimizedTacPanel);

        add(mainPanel);
    }

    private DefaultTableModel getTACTableModel(List<TACInstruction> instructions) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Result");
        model.addColumn("Operator");
        model.addColumn("Operand 1");
        model.addColumn("Operand 2");
        for (TACInstruction instruction : instructions) {
            String result = instruction.getResult() == null ? "NULL" : instruction.getResult().toString();
            String op1 = instruction.getOp1() == null ? "NULL" : instruction.getOp1().toString();
            String op2 = instruction.getOp2() == null ? "NULL" : instruction.getOp2().toString();
            String operator = instruction.getOperation() == null ? "NULL" : instruction.getOperation().toString();
            model.addRow(new Object[]{result, operator, op1, op2});
        }
        return model;
    }
} 