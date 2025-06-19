package org.main;

import Symbols.Symbol;
import Symbols.SymbolTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SymbolTableWindow extends JFrame {
    private JTable symbolTable;
    private SymbolTable symTable;

    public SymbolTableWindow(SymbolTable symTable) {
        this.symTable = symTable;
        initComponents();
        setTitle("Symbol Table");
        setPreferredSize(new Dimension(600, 400));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240)); // Light gray

        // Create Label for Symbol Table
        JLabel symbolTableLabel = new JLabel("Symbol Table");
        symbolTableLabel.setHorizontalAlignment(SwingConstants.CENTER);
        symbolTableLabel.setForeground(Color.BLACK);
        symbolTableLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        // Create Scroll Pane for Symbol Table
        symbolTable = new JTable();
        JScrollPane symbolTableScrollPane = new JScrollPane(symbolTable);
        symbolTableScrollPane.setBorder(null);
        symbolTableScrollPane.setBackground(new Color(240, 240, 240)); // Light gray
        symbolTableScrollPane.getViewport().setBackground(new Color(240, 240, 240)); // Light gray

        // Build Symbol Table
        symbolTable.setModel(getSymbolTableModel());
        symbolTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        symbolTable.setBackground(Color.WHITE);
        symbolTable.setForeground(Color.BLACK);
        symbolTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        symbolTable.getTableHeader().setForeground(Color.BLACK);

        // Add components to panel
        mainPanel.add(symbolTableLabel, BorderLayout.NORTH);
        mainPanel.add(symbolTableScrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private DefaultTableModel getSymbolTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Identifier");
        model.addColumn("Type");
        model.addColumn("Scope Level");
        // Add info to the table, from symbol table map
        for (Symbol symbol : symTable.getSymbols().values()) {
            model.addRow(new Object[]{symbol.getIdentifier(), symbol.getType(), symbol.getScopeLevel()});
        }
        return model;
    }
} 