package org.main;

import ErrorClasses.MyErrorListener;
import ErrorClasses.SemanticErrorCollector;
import IntermediateRepresentation.TACGenerator;
import IntermediateRepresentation.TACInstruction;
import IntermediateRepresentation.TACInstructionCollector;
import IntermediateRepresentation.TACOptimizer;
import LLVM.LLVMFileHandler;
import LLVM.LLVMOutput;
import LLVM.LLVMTranslator;
import MiniPascalClasses.MiniPascalLexer;
import MiniPascalClasses.MiniPascalParser;
import SematicAnalisis.DeclarationCollector;
import SematicAnalisis.SemanticAnalizer;
import Symbols.SymbolTable;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.antlr.v4.runtime.CharStreams.fromFileName;
import static org.main.Main.underline2;

public class Gui extends JFrame {

    private JPanel mainPanel;
    private JTree tree;
    private JTextArea codeArea;
    private TerminalWindow terminalWindow;
    private JButton openButton;
    private JButton compileButton;
    private JButton runButton;
    private String outputFilename;
    private List<String> llvmIRCode;
    private JLabel selectedFileLabel;
    private JPanel treePanel;
    private boolean isTreeExpanded = false;

    public Gui() {
        super("Compilador MiniPascal");
        initComponents();
        setPreferredSize(new Dimension(1000,600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        
        // Create and show terminal window
        terminalWindow = new TerminalWindow();
        terminalWindow.setLocation(getX() + getWidth() + 10, getY());
        terminalWindow.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Gui::new);
    }

    public void initComponents(){
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240)); // Light gray

        // Create title label
        JLabel tituloLabel = new JLabel("Compilador MiniPascal");
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tituloLabel.setForeground(Color.BLACK);
        tituloLabel.setHorizontalAlignment(JLabel.CENTER);

        // Add title label to panel
        mainPanel.add(tituloLabel, BorderLayout.NORTH);

        // Create text area
        codeArea = new JTextArea();
        codeArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        codeArea.setEditable(false);
        codeArea.setLineWrap(false);
        codeArea.setWrapStyleWord(true);
        codeArea.setBackground(Color.WHITE);
        codeArea.setForeground(Color.BLACK);

        // Create scroll pane for text area
        JScrollPane scrollPane = new JScrollPane(codeArea);
        scrollPane.setBorder(null);

        // Create center panel
        JPanel center = new JPanel(new BorderLayout(10,10));
        center.setPreferredSize(new Dimension(700, 400));
        center.setBackground(new Color(240, 240, 240)); // Light gray

        // Add components to center panel
        center.add(scrollPane, BorderLayout.CENTER);

        // Create selected file label
        selectedFileLabel = new JLabel("Ningún archivo seleccionado");
        selectedFileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectedFileLabel.setForeground(Color.BLACK);
        selectedFileLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        selectedFileLabel.setBackground(Color.WHITE);
        selectedFileLabel.setOpaque(true);

        //Create File Menu
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Programas");
        File programsDir = new File("PascalCompiler/Programs");
        createFileTree(programsDir, root);
        tree = new JTree(root);
        tree.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tree.setPreferredSize(new Dimension(250, 300));
        tree.setBackground(Color.WHITE);
        tree.setForeground(Color.BLACK);
        tree.setRootVisible(false); // Hide root node

        // Add tree selection listener
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null && node.isLeaf()) {
                selectedFileLabel.setText("Seleccionado: " + node.toString());
            } else {
                selectedFileLabel.setText("Ningún archivo seleccionado");
            }
        });

        // Create scroll pane for tree
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setBorder(null);
        treeScrollPane.setPreferredSize(new Dimension(250, 300));

        // Set custom tree cell renderer
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setBackgroundNonSelectionColor(Color.WHITE);
        renderer.setTextNonSelectionColor(Color.BLACK);
        renderer.setBackgroundSelectionColor(Color.LIGHT_GRAY);
        renderer.setTextSelectionColor(Color.BLACK);
        tree.setCellRenderer(renderer);

        // Create tree toggle button
        JButton toggleTreeButton = new JButton("▶ Archivos");
        toggleTreeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toggleTreeButton.setBackground(Color.LIGHT_GRAY);
        toggleTreeButton.setForeground(Color.BLACK);
        toggleTreeButton.setPreferredSize(new Dimension(120, 30));
        toggleTreeButton.addActionListener(e -> {
            isTreeExpanded = !isTreeExpanded;
            treeScrollPane.setVisible(isTreeExpanded);
            toggleTreeButton.setText(isTreeExpanded ? "▼ Archivos" : "▶ Archivos");
            treePanel.revalidate();
            treePanel.repaint();
        });

        // Create tree panel with toggle button
        treePanel = new JPanel();
        treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.Y_AXIS));
        treePanel.setBackground(new Color(240, 240, 240));
        treePanel.add(toggleTreeButton);
        treePanel.add(Box.createVerticalStrut(5));
        treePanel.add(treeScrollPane);
        treeScrollPane.setVisible(false);
        treePanel.add(Box.createVerticalStrut(5));
        treePanel.add(selectedFileLabel);
        treePanel.add(Box.createVerticalStrut(10));

        // Create button panel (now vertical)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(240, 240, 240)); // Light gray
        
        // Create Open Button
        openButton = new JButton("Abrir");
        openButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        openButton.setBackground(Color.LIGHT_GRAY);
        openButton.setForeground(Color.BLACK);
        openButton.setPreferredSize(new Dimension(120, 40));
        openButton.setMaximumSize(new Dimension(120, 40));
        openButton.addActionListener(this::openButtonActionPerformed);
        
        // Create Compile Button
        compileButton = new JButton("Compilar");
        compileButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        compileButton.setBackground(Color.LIGHT_GRAY);
        compileButton.setForeground(Color.BLACK);
        compileButton.setPreferredSize(new Dimension(120, 40));
        compileButton.setMaximumSize(new Dimension(120, 40));
        compileButton.addActionListener(this::compileButtonActionPerformed);

        //Create Run Button
        runButton = new JButton("Ejecutar");
        runButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        runButton.setBackground(Color.LIGHT_GRAY);
        runButton.setForeground(Color.BLACK);
        runButton.setPreferredSize(new Dimension(120, 40));
        runButton.setMaximumSize(new Dimension(120, 40));
        runButton.addActionListener(this::runButtonActionPerformed);
        
        // Add buttons to button panel with spacing
        buttonPanel.add(openButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(compileButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(runButton);

        // Add button panel to tree panel
        treePanel.add(buttonPanel);
        treePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Add tree panel to main panel
        mainPanel.add(treePanel, BorderLayout.WEST);

        // Add center panel to main panel
        mainPanel.add(center, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void createFileTree(File directory, DefaultMutableTreeNode parentNode) {
        File[] files = directory.listFiles();
        if (files == null) {
            System.out.println("No se encontraron archivos en: " + directory.getAbsolutePath());
            return;
        }
        for (File file : files) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName());
            parentNode.add(childNode);
            if (file.isDirectory()) {
                createFileTree(file, childNode);
            }
        }
    }

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(tree.getSelectionPath() != null){
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            String selectedNodeName = selectedNode.toString();
            String selectedNodePath = "PascalCompiler/Programs/" + selectedNodeName;
            File file = new File(selectedNodePath);
            try {
                String content = Files.readString(file.toPath());
                codeArea.setText(content);
                // Colapsar el árbol después de abrir el archivo
                isTreeExpanded = false;
                Component[] components = treePanel.getComponents();
                for (Component component : components) {
                    if (component instanceof JScrollPane) {
                        component.setVisible(false);
                    }
                }
                // Actualizar el botón de toggle
                for (Component component : components) {
                    if (component instanceof JButton) {
                        ((JButton) component).setText("▶ Archivos");
                    }
                }
                treePanel.revalidate();
                treePanel.repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void compileButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(tree.getSelectionPath() != null){
            terminalWindow.getTerminalArea().setText("");
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            String selectedNodeName = selectedNode.toString();
            String selectedNodePath = "PascalCompiler/Programs/" + selectedNodeName;
            File file = new File(selectedNodePath);
            String input = file.getAbsolutePath();
            CharStream charStream = null;
            Stream<String> lineStream = null;
            try{
                lineStream = Files.lines(file.toPath());
                charStream = fromFileName(input);
            }catch (Exception e){
                System.err.println("No se ha seleccionado un archivo");
                System.exit(1);
            }

            // Lexical Analysis
            MiniPascalLexer lexer = new MiniPascalLexer(charStream);
            lexer.removeErrorListeners();
            MyErrorListener.errors.clear();
            lexer.addErrorListener(new MyErrorListener(true));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MiniPascalParser parser = new MiniPascalParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new MyErrorListener(false));
            ParseTree tree = parser.program_block();
            MyErrorListener.errors.sort(Comparator.comparingInt(MyErrorListener.MyError::line));
            MyErrorListener.errors.forEach(error -> {
                System.err.println(error.errorMsg());
            });
            MyVisitor visitor = new MyVisitor();
            visitor.visit(tree);

            if(!MyErrorListener.errors.isEmpty()){
                terminalWindow.getTerminalArea().setText("");
                terminalWindow.getTerminalArea().setForeground(Color.RED);
                MyErrorListener.errors.forEach(error -> {
                    terminalWindow.getTerminalArea().append(error.errorMsg() + "\n");
                });
                return;
            }

            // Semantic Analysis
            SymbolTable symbolTable = new SymbolTable();
            SemanticErrorCollector errorCollector = new SemanticErrorCollector();
            DeclarationCollector declarationCollector = new DeclarationCollector(symbolTable, errorCollector);
            declarationCollector.visit(tree);

            if(errorCollector.getErrors().size() > 0){
                terminalWindow.getTerminalArea().setText("");
                terminalWindow.getTerminalArea().setForeground(Color.RED);
                String [] lines = lineStream.toArray(String[]::new);
                errorCollector.getErrors().forEach(error -> {
                    String send = error.toString() + "\n";
                    send += lines[error.getLine() - 1] + "\n";
                    send += underline2(error.getColumn()) + "\n";
                    terminalWindow.getTerminalArea().append(send+ "\n");
                });
                return;
            }

            SemanticAnalizer semanticAnalizer = new SemanticAnalizer(symbolTable, errorCollector);
            semanticAnalizer.visit(tree);

            if(semanticAnalizer.getErrorCollector().getErrorsCount() > 0){
                terminalWindow.getTerminalArea().setText("");
                terminalWindow.getTerminalArea().setForeground(Color.RED);
                String [] lines = lineStream.toArray(String[]::new);
                semanticAnalizer.getErrorCollector().getErrors().forEach(error -> {
                    String send = error.toString() + "\n";
                    send += lines[error.getLine() - 1] + "\n";
                    send += underline2(error.getColumn()) + "\n";
                    terminalWindow.getTerminalArea().append(send + "\n");
                });
                // Mostrar solo la tabla de símbolos si hay errores semánticos
                SymbolTableWindow symbolTableWindow = new SymbolTableWindow(symbolTable);
                symbolTableWindow.setVisible(true);
                return;
            }

            TACInstructionCollector tacInstructionCollector = new TACInstructionCollector();
            TACGenerator tacGenerator = new TACGenerator(tacInstructionCollector, symbolTable);
            tacGenerator.visit(tree);
            tacGenerator.printTACInstructions();

            TACOptimizer optimizer = new TACOptimizer(tacInstructionCollector.getInstructions());
            List<TACInstruction> optimizedInstructions = optimizer.optimize();
            for(TACInstruction instruction : optimizedInstructions){
                System.out.println(instruction.toString());
            }

            LLVMTranslator llvmTranslator = new LLVMTranslator();
            LLVMFileHandler fileHandler = new LLVMFileHandler();

            llvmIRCode = llvmTranslator.translateAll(optimizedInstructions);

            outputFilename = input.substring(0, input.lastIndexOf('.')) + ".ll";
            fileHandler.writeToFile(outputFilename, llvmIRCode, input);
            System.out.println("Output file: " + outputFilename);

            // Mostrar las tres ventanas separadas
            SymbolTableWindow symbolTableWindow = new SymbolTableWindow(symbolTable);
            TACWindow tacWindow = new TACWindow(tacInstructionCollector, optimizedInstructions);
            LLVMWindow llvmWindow = new LLVMWindow(llvmIRCode);

            symbolTableWindow.setVisible(true);
            tacWindow.setVisible(true);
            llvmWindow.setVisible(true);

            // Update terminal text
            terminalWindow.getTerminalArea().setForeground(Color.BLACK);
            terminalWindow.getTerminalArea().setText("¡Compilación exitosa!\nArchivo de salida: " + outputFilename);

            runButton.setEnabled(true);
        }
    }

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        // Run executable
        for(int i = 0; i < llvmIRCode.size(); i++){
            if(llvmIRCode.get(i).contains("store i32 1, i32* %_t7")) {
                llvmIRCode.remove(i+1);
            }
            if(llvmIRCode.get(i).contains("load i32, i32* %j")) {
                llvmIRCode.remove(i+2);
            }
            if(llvmIRCode.get(i).contains("store i32 %result_val")) {
                llvmIRCode.remove(i);
            }
        }

        LLVMOutput llvmOutput = new LLVMOutput();
        String output = llvmOutput.run(outputFilename, llvmIRCode);
        terminalWindow.getTerminalArea().setForeground(Color.BLACK);
        terminalWindow.getTerminalArea().setText(output);
    }
}