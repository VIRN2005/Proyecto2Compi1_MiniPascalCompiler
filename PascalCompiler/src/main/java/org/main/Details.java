package org.main;

import IntermediateRepresentation.TACInstruction;
import IntermediateRepresentation.TACInstructionCollector;
import Symbols.Symbol;
import Symbols.SymbolTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Details extends JFrame {
    private JTabbedPane tabbedPane;
    private JTextArea LLVMTextArea;
    private JTable symbolTable;
    private JTable TAC;
    private JTable optimizedTACTable;
    private JTextArea TACCodeTextArea;
    private JTextArea optimizedTACCodeTextArea;
    private SymbolTable symTable;
    private TACInstructionCollector tacInstructionCollector;
    private List<TACInstruction> optimizedTacCode;
    private List<String> llvmIRCode;

    public Details(SymbolTable symTable, TACInstructionCollector tacInstructionCollector, List<TACInstruction> optimizedTacCode, List<String> llvmIRCode) {
        this.symTable = symTable;
        this.tacInstructionCollector = tacInstructionCollector;
        this.optimizedTacCode = optimizedTacCode;
        this.llvmIRCode = llvmIRCode;
        setBackground(new Color(102, 0, 102));
        if(tacInstructionCollector == null){
            onlySymTable();
        }else {
            initComponents();
        }
        setTitle("Details");
        setPreferredSize(new Dimension(1000, 800));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    public void initComponents(){
        tabbedPane = new JTabbedPane();
        LLVMTextArea = new JTextArea();
        symbolTable = new JTable();
        TAC = new JTable();
        optimizedTACTable = new JTable();
        TACCodeTextArea = new JTextArea();
        optimizedTACCodeTextArea = new JTextArea();

        //Create Pane for Symbol Table
        JPanel symbolTablePane = new JPanel();
        symbolTablePane.setLayout(new BorderLayout());
        symbolTablePane.setBackground(new Color(102, 0, 102));

        //Create Label for Symbol Table
        JLabel symbolTableLabel = new JLabel("Symbol Table");
        symbolTableLabel.setHorizontalAlignment(SwingConstants.CENTER);
        symbolTableLabel.setForeground(Color.WHITE);
        symbolTableLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        //Create Scroll Pane for Symbol Table
        JScrollPane symbolTableScrollPane = new JScrollPane(symbolTable);
        symbolTableScrollPane.setBorder(null);
        symbolTableScrollPane.setBackground(new Color(102, 0, 102));
        symbolTableScrollPane.getViewport().setBackground(new Color(102, 0, 102));

        //Build Symbol Table
        symbolTable.setModel(getSymbolTableModel());
        symbolTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        symbolTable.setBackground(Color.BLACK);
        symbolTable.setForeground(Color.WHITE);
        symbolTable.getTableHeader().setBackground(Color.BLACK);
        symbolTable.getTableHeader().setForeground(Color.WHITE);

        //Add Label and Scroll Pane to Symbol Table Pane
        symbolTablePane.add(symbolTableLabel, BorderLayout.NORTH);
        symbolTablePane.add(symbolTableScrollPane, BorderLayout.CENTER);


        tabbedPane.addTab("Symbol Table", symbolTablePane);

        //Create Pane for TAC and Optimized TAC
        JPanel TACPane = new JPanel();
        TACPane.setLayout(new GridLayout(1, 2, 10, 10));
        TACPane.setBackground(new Color(102, 0, 102));

        //Create Label for TAC
        JLabel TACLabel = new JLabel("TAC");
        TACLabel.setHorizontalAlignment(SwingConstants.CENTER);
        TACLabel.setForeground(Color.WHITE);
        TACLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        //Create Label for Optimized TAC
        JLabel optimizedTACLabel = new JLabel("Optimized TAC");
        optimizedTACLabel.setHorizontalAlignment(SwingConstants.CENTER);
        optimizedTACLabel.setForeground(Color.WHITE);
        optimizedTACLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        //Create Scroll Pane for TAC
        JScrollPane TACScrollPane = new JScrollPane(TAC);
        TACScrollPane.setBorder(null);
        TACScrollPane.setBackground(new Color(102, 0, 102));
        TACScrollPane.getViewport().setBackground(new Color(102, 0, 102));

        //Create Scroll Pane for Optimized TAC
        JScrollPane optimizedTACScrollPane = new JScrollPane(optimizedTACTable);
        optimizedTACScrollPane.setBorder(null);
        optimizedTACScrollPane.setBackground(new Color(102, 0, 102));
        optimizedTACScrollPane.getViewport().setBackground(new Color(102, 0, 102));

        //Build TAC
        TAC.setModel(getTACTableModel(tacInstructionCollector.getInstructions()));
        TAC.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TAC.setBackground(Color.BLACK);
        TAC.setForeground(Color.WHITE);
        TAC.getTableHeader().setBackground(Color.BLACK);
        TAC.getTableHeader().setForeground(Color.WHITE);

        //Build Optimized TAC
        optimizedTACTable.setModel(getTACTableModel(optimizedTacCode));
        optimizedTACTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        optimizedTACTable.setBackground(Color.BLACK);
        optimizedTACTable.setForeground(Color.WHITE);
        optimizedTACTable.getTableHeader().setBackground(Color.BLACK);
        optimizedTACTable.getTableHeader().setForeground(Color.WHITE);

        //Create TAC Pane
        JPanel TAC = new JPanel();
        TAC.setLayout(new BorderLayout());
        TAC.setBackground(new Color(102, 0, 102));

        //Create Optimized TAC Pane
        JPanel optimizedTAC = new JPanel();
        optimizedTAC.setLayout(new BorderLayout());
        optimizedTAC.setBackground(new Color(102, 0, 102));

        //Add Label and Scroll Pane to TAC Pane
        TAC.add(TACLabel, BorderLayout.NORTH);
        TAC.add(TACScrollPane, BorderLayout.CENTER);

        //Add Label and Scroll Pane to Optimized TAC Pane
        optimizedTAC.add(optimizedTACLabel, BorderLayout.NORTH);
        optimizedTAC.add(optimizedTACScrollPane, BorderLayout.CENTER);

        //Add TAC and Optimized TAC Panes to TAC Pane
        TACPane.add(TAC);
        TACPane.add(optimizedTAC);

        tabbedPane.addTab("TAC", TACPane);

        //Create Pane for TAC Code
        JPanel TACCodePane = new JPanel();
        TACCodePane.setLayout(new GridLayout(1, 2, 10, 10));
        TACCodePane.setBackground(new Color(102, 0, 102));

        //Create Label for TAC Code
        JLabel TACCodeLabel = new JLabel("TAC Code");
        TACCodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        TACCodeLabel.setForeground(Color.WHITE);
        TACCodeLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        //Create Label for Optimized TAC Code
        JLabel optimizedTACCodeLabel = new JLabel("Optimized TAC Code");
        optimizedTACCodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        optimizedTACCodeLabel.setForeground(Color.WHITE);
        optimizedTACCodeLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        //Create Scroll Pane for TAC Code
        JScrollPane TACCodeScrollPane = new JScrollPane(TACCodeTextArea);
        TACCodeScrollPane.setBorder(null);
        TACCodeScrollPane.setBackground(new Color(102, 0, 102));
        TACCodeScrollPane.getViewport().setBackground(new Color(102, 0, 102));

        //Create Scroll Pane for Optimized TAC Code
        JScrollPane optimizedTACCodeScrollPane = new JScrollPane(optimizedTACCodeTextArea);
        optimizedTACCodeScrollPane.setBorder(null);
        optimizedTACCodeScrollPane.setBackground(new Color(102, 0, 102));
        optimizedTACCodeScrollPane.getViewport().setBackground(new Color(102, 0, 102));

        //Build TAC Code
        StringBuilder tacCode = new StringBuilder();
        for (TACInstruction instruction : tacInstructionCollector.getInstructions()) {
            tacCode.append(instruction.toString()).append("\n");
        }
        TACCodeTextArea.setText(tacCode.toString());
        TACCodeTextArea.setEditable(false);
        TACCodeTextArea.setBackground(Color.BLACK);
        TACCodeTextArea.setForeground(Color.WHITE);
        TACCodeTextArea.setFont(new Font("Consolas", Font.PLAIN, 16));

        //Build Optimized TAC Code
        StringBuilder optimizedTACCode = new StringBuilder();
        for (TACInstruction instruction : optimizedTacCode) {
            optimizedTACCode.append(instruction.toString()).append("\n");
        }
        optimizedTACCodeTextArea.setText(optimizedTACCode.toString());
        optimizedTACCodeTextArea.setEditable(false);
        optimizedTACCodeTextArea.setBackground(Color.BLACK);
        optimizedTACCodeTextArea.setForeground(Color.WHITE);
        optimizedTACCodeTextArea.setFont(new Font("Consolas", Font.PLAIN, 16));

        //Create TAC Code Pane
        JPanel TACCode = new JPanel();
        TACCode.setLayout(new BorderLayout());
        TACCode.setBackground(new Color(102, 0, 102));

        //Create Optimized TAC Code Pane
        JPanel optimizedTACCodePane = new JPanel();
        optimizedTACCodePane.setLayout(new BorderLayout());
        optimizedTACCodePane.setBackground(new Color(102, 0, 102));

        //Add Label and Scroll Pane to TAC Code Pane
        TACCode.add(TACCodeLabel, BorderLayout.NORTH);
        TACCode.add(TACCodeScrollPane, BorderLayout.CENTER);

        //Add Label and Scroll Pane to Optimized TAC Code Pane
        optimizedTACCodePane.add(optimizedTACCodeLabel, BorderLayout.NORTH);
        optimizedTACCodePane.add(optimizedTACCodeScrollPane, BorderLayout.CENTER);

        //Add TAC Code and Optimized TAC Code Panes to TAC Code Pane
        TACCodePane.add(TACCode);
        TACCodePane.add(optimizedTACCodePane);

        tabbedPane.addTab("TAC Code", TACCodePane);

        //Create Pane for LLVM IR
        JPanel LLVMIRPane = new JPanel();
        LLVMIRPane.setLayout(new BorderLayout());
        LLVMIRPane.setBackground(new Color(102, 0, 102));

        //Create Label for LLVM IR
        JLabel LLVMIRLabel = new JLabel("LLVM IR");
        LLVMIRLabel.setHorizontalAlignment(SwingConstants.CENTER);
        LLVMIRLabel.setForeground(Color.WHITE);
        LLVMIRLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        //Create Scroll Pane for LLVM IR
        JScrollPane LLVMIRScrollPane = new JScrollPane(LLVMTextArea);
        LLVMIRScrollPane.setBorder(null);
        LLVMIRScrollPane.setBackground(new Color(102, 0, 102));
        LLVMIRScrollPane.getViewport().setBackground(new Color(102, 0, 102));

        //Build LLVM IR
        StringBuilder llvmIR = new StringBuilder();
        for (String line : llvmIRCode) {
            llvmIR.append(line).append("\n");
        }
        LLVMTextArea.setText(llvmIR.toString());
        LLVMTextArea.setEditable(false);
        LLVMTextArea.setBackground(Color.BLACK);
        LLVMTextArea.setForeground(Color.WHITE);
        LLVMTextArea.setFont(new Font("Consolas", Font.PLAIN, 16));

        //Add Label and Scroll Pane to LLVM IR Pane
        LLVMIRPane.add(LLVMIRLabel, BorderLayout.NORTH);
        LLVMIRPane.add(LLVMIRScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("LLVM IR", LLVMIRPane);

        this.add(tabbedPane);
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public DefaultTableModel getSymbolTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Identifier");
        model.addColumn("Type");
        model.addColumn("Scope Level");
        //Add info to the table, from symbol table map
        for (Symbol symbol : symTable.getSymbols().values()) {
            model.addRow(new Object[]{symbol.getIdentifier(), symbol.getType(), symbol.getScopeLevel()});
        }
        return model;
    }

    public DefaultTableModel getTACTableModel(List<TACInstruction> instructions) {
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

    public static void main(String[] args) {
        Details details = new Details(null, null, null, null);
        details.initComponents();
    }

    public void onlySymTable(){
        tabbedPane = new JTabbedPane();
        LLVMTextArea = new JTextArea();
        symbolTable = new JTable();
        TAC = new JTable();
        optimizedTACTable = new JTable();
        TACCodeTextArea = new JTextArea();
        optimizedTACCodeTextArea = new JTextArea();

        //Create Pane for Symbol Table
        JPanel symbolTablePane = new JPanel();
        symbolTablePane.setLayout(new BorderLayout());
        symbolTablePane.setBackground(new Color(102, 0, 102));

        //Create Label for Symbol Table
        JLabel symbolTableLabel = new JLabel("Symbol Table");
        symbolTableLabel.setHorizontalAlignment(SwingConstants.CENTER);
        symbolTableLabel.setForeground(Color.WHITE);
        symbolTableLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        //Create Scroll Pane for Symbol Table
        JScrollPane symbolTableScrollPane = new JScrollPane(symbolTable);
        symbolTableScrollPane.setBorder(null);
        symbolTableScrollPane.setBackground(new Color(102, 0, 102));
        symbolTableScrollPane.getViewport().setBackground(new Color(102, 0, 102));

        //Build Symbol Table
        symbolTable.setModel(getSymbolTableModel());
        symbolTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        symbolTable.setBackground(Color.BLACK);
        symbolTable.setForeground(Color.WHITE);
        symbolTable.getTableHeader().setBackground(Color.BLACK);
        symbolTable.getTableHeader().setForeground(Color.WHITE);

        //Add Label and Scroll Pane to Symbol Table Pane
        symbolTablePane.add(symbolTableLabel, BorderLayout.NORTH);
        symbolTablePane.add(symbolTableScrollPane, BorderLayout.CENTER);


        tabbedPane.addTab("Symbol Table", symbolTablePane);

        this.add(tabbedPane);
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }


}
