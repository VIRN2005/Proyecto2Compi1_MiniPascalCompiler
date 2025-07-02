package org.main;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.antlr.v4.runtime.CharStream;
import static org.antlr.v4.runtime.CharStreams.fromFileName;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import static org.main.Main.underline2;

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

public class Gui extends JFrame {

    // Colores del tema hacker
    private static final Color DARK_BG = new Color(15, 15, 15);           // Fondo principal ultra oscuro
    private static final Color DARKER_BG = new Color(8, 8, 8);           // Fondo más oscuro
    private static final Color LIME_GREEN = new Color(0, 255, 127);       // Verde lima brillante
    private static final Color DARK_LIME = new Color(0, 200, 100);        // Verde lima oscuro
    private static final Color ACCENT_GREEN = new Color(0, 150, 75);      // Verde acento
    private static final Color TEXT_GREEN = new Color(0, 255, 100);       // Verde texto
    private static final Color BORDER_GREEN = new Color(0, 180, 90);      // Verde bordes
    private static final Color HOVER_GREEN = new Color(0, 120, 60);       // Verde hover
    private static final Color TERMINAL_GREEN = new Color(0, 255, 0);     // Verde terminal clásico

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
        super("◉ MiniPascal Compiler v2.0 ◉");

        // Configurar look and feel oscuro
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initComponents();
        setPreferredSize(new Dimension(1200, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setUndecorated(false);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);

        // Crear terminal con estilo hacker
        terminalWindow = new TerminalWindow();
        terminalWindow.setLocation(getX() + getWidth() + 10, getY());
        terminalWindow.setVisible(true);
    }

    public static void main(String[] args) {
        // Configurar propiedades del sistema para mejor renderizado
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Gui();
        });
    }

    public void initComponents(){
        // Panel principal con gradiente
        mainPanel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradiente sutil de fondo
                GradientPaint gradient = new GradientPaint(
                        0, 0, DARK_BG,
                        getWidth(), getHeight(), DARKER_BG
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Efecto de líneas de código Matrix
                g2d.setColor(new Color(0, 255, 127, 20));
                for (int i = 0; i < getWidth(); i += 100) {
                    g2d.drawLine(i, 0, i, getHeight());
                }

                g2d.dispose();
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título mejorado - más legible con fondo sólido
        JLabel tituloLabel = new JLabel("MINIPASCAL COMPILER") {
            private float glow = 0f;
            private Timer timer;
            {
                timer = new Timer(30, e -> {
                    glow += 0.08f;
                    if (glow > 2 * Math.PI) glow = 0f;
                    repaint();
                });
                timer.start();
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Fondo sólido para mejor legibilidad
                g2d.setColor(new Color(0, 0, 0, 220));
                g2d.fillRoundRect(20, 10, getWidth() - 40, getHeight() - 20, 20, 20);

                // Glow animado verde
                int glowSize = 18 + (int)(Math.sin(glow) * 6);
                for (int i = glowSize; i > 0; i -= 2) {
                    g2d.setColor(new Color(0, 255, 127, 18));
                    g2d.setStroke(new BasicStroke(i));
                    g2d.drawRoundRect(20 - i/2, 10 - i/2, getWidth() - 40 + i, getHeight() - 20 + i, 20 + i, 20 + i);
                }

                // Texto principal con efecto neón
                g2d.setFont(new Font("Consolas", Font.BOLD, 38));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 5;

                // Sombra verde glow
                for (int i = 6; i > 0; i--) {
                    g2d.setColor(new Color(0, 255, 127, 30));
                    g2d.drawString(getText(), x - i, y);
                    g2d.drawString(getText(), x + i, y);
                    g2d.drawString(getText(), x, y - i);
                    g2d.drawString(getText(), x, y + i);
                }

                // Texto blanco central
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };
        tituloLabel.setHorizontalAlignment(JLabel.CENTER);
        tituloLabel.setOpaque(false);
        tituloLabel.setPreferredSize(new Dimension(0, 100));
        mainPanel.add(tituloLabel, BorderLayout.NORTH);

        // Área de código con estilo terminal
        codeArea = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo con efecto scanlines
                g2d.setColor(DARKER_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Líneas de escaneo sutiles
                g2d.setColor(new Color(0, 255, 127, 10));
                for (int y = 0; y < getHeight(); y += 2) {
                    g2d.drawLine(0, y, getWidth(), y);
                }

                g2d.dispose();
                super.paintComponent(g);
            }
        };
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeArea.setEditable(false);
        codeArea.setLineWrap(false);
        codeArea.setWrapStyleWord(true);
        codeArea.setBackground(DARKER_BG);
        codeArea.setForeground(TEXT_GREEN);
        codeArea.setCaretColor(LIME_GREEN);
        codeArea.setSelectionColor(HOVER_GREEN);
        codeArea.setSelectedTextColor(Color.WHITE);

        // ScrollPane personalizado
        JScrollPane scrollPane = new JScrollPane(codeArea) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(DARK_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        scrollPane.setBorder(new GlowBorder(BORDER_GREEN, 2));
        scrollPane.getViewport().setBackground(DARKER_BG);

        // Personalizar scrollbars
        scrollPane.getVerticalScrollBar().setUI(new HackerScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new HackerScrollBarUI());

        // Panel central
        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setPreferredSize(new Dimension(800, 450));
        center.setOpaque(false);
        center.add(scrollPane, BorderLayout.CENTER);

        // Label de archivo seleccionado
        selectedFileLabel = new JLabel(">>> Ningún archivo seleccionado") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo con gradiente
                GradientPaint gradient = new GradientPaint(
                        0, 0, DARKER_BG,
                        getWidth(), 0, DARK_BG
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Borde brillante
                g2d.setColor(BORDER_GREEN);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

                g2d.dispose();
                super.paintComponent(g);
            }
        };
        selectedFileLabel.setFont(new Font("Courier New", Font.BOLD, 12));
        selectedFileLabel.setForeground(TEXT_GREEN);
        selectedFileLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        selectedFileLabel.setOpaque(false);
        selectedFileLabel.setPreferredSize(new Dimension(0, 35));

        // Árbol de archivos con estilo hacker
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("◉ PROGRAMS");
        File programsDir = new File("PascalCompiler/Programs");
        createFileTree(programsDir, root);
        tree = new JTree(root);
        tree.setFont(new Font("Courier New", Font.PLAIN, 12));
        tree.setPreferredSize(new Dimension(280, 350));
        tree.setBackground(DARKER_BG);
        tree.setForeground(TEXT_GREEN);
        tree.setRootVisible(true);

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null && node.isLeaf()) {
                selectedFileLabel.setText("> Seleccionado: " + node.toString());
            } else {
                selectedFileLabel.setText(">>> Ningún archivo seleccionado");
            }
        });

        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setBorder(new GlowBorder(BORDER_GREEN, 1));
        treeScrollPane.setPreferredSize(new Dimension(280, 350));
        treeScrollPane.getViewport().setBackground(DARKER_BG);
        treeScrollPane.getVerticalScrollBar().setUI(new HackerScrollBarUI());
        treeScrollPane.getHorizontalScrollBar().setUI(new HackerScrollBarUI());

        // Renderer personalizado para el árbol
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                          boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                setBackgroundNonSelectionColor(DARKER_BG);
                setTextNonSelectionColor(TEXT_GREEN);
                setBackgroundSelectionColor(HOVER_GREEN);
                setTextSelectionColor(Color.WHITE);
                setBorderSelectionColor(LIME_GREEN);

                if (leaf) {
                    setText("📄 " + value.toString());
                } else {
                    setText(expanded ? "📂 " + value.toString() : "📁 " + value.toString());
                }

                return this;
            }
        };
        tree.setCellRenderer(renderer);

        // Botón toggle con animación - TAMAÑO AUMENTADO
        JButton toggleTreeButton = createHackerButton(">> ARCHIVOS <<", new Dimension(200, 50));
        toggleTreeButton.addActionListener(e -> {
            isTreeExpanded = !isTreeExpanded;
            treeScrollPane.setVisible(isTreeExpanded);
            toggleTreeButton.setText(isTreeExpanded ? ">> OCULTAR <<" : ">> ARCHIVOS <<");
            treePanel.revalidate();
            treePanel.repaint();
        });

        // Panel del árbol
        treePanel = new JPanel();
        treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.Y_AXIS));
        treePanel.setOpaque(false);
        treePanel.add(toggleTreeButton);
        treePanel.add(Box.createVerticalStrut(10));
        treePanel.add(treeScrollPane);
        treeScrollPane.setVisible(false);
        treePanel.add(Box.createVerticalStrut(10));
        treePanel.add(selectedFileLabel);
        treePanel.add(Box.createVerticalStrut(15));

        // Botones con estilo hacker - TAMAÑOS AUMENTADOS
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        openButton = createHackerButton("ABRIR", new Dimension(200, 60));
        openButton.addActionListener(this::openButtonActionPerformed);

        compileButton = createHackerButton("COMPILAR", new Dimension(200, 60));
        compileButton.addActionListener(this::compileButtonActionPerformed);

        runButton = createHackerButton("EJECUTAR", new Dimension(200, 60));
        runButton.addActionListener(this::runButtonActionPerformed);

        buttonPanel.add(openButton);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(compileButton);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(runButton);

        treePanel.add(buttonPanel);
        treePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        mainPanel.add(treePanel, BorderLayout.WEST);
        mainPanel.add(center, BorderLayout.CENTER);

        add(mainPanel);

        // Configurar el frame
        getContentPane().setBackground(DARK_BG);
    }

        private JButton createHackerButton(String text, Dimension size) {
        JButton button = new JButton(text) {
            private float glow = 0f;
            private Timer timer;
            {
                timer = new Timer(30, e -> {
                    if (getModel().isRollover()) {
                        glow += 0.15f;
                        if (glow > 2 * Math.PI) glow = 0f;
                        repaint();
                    }
                });
                timer.start();
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Fondo degradado
                GradientPaint gp = new GradientPaint(
                    0, 0, getModel().isRollover() ? new Color(0, 255, 127, 90) : DARKER_BG,
                    0, getHeight(), getModel().isRollover() ? new Color(0, 255, 127, 180) : DARK_BG
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                // Glow animado
                if (getModel().isRollover()) {
                    int glowSize = 10 + (int)(Math.abs(Math.sin(glow)) * 8);
                    for (int i = glowSize; i > 0; i -= 2) {
                        g2d.setColor(new Color(0, 255, 127, 18));
                        g2d.setStroke(new BasicStroke(i));
                        g2d.drawRoundRect(0 - i/2, 0 - i/2, getWidth() + i, getHeight() + i, 18 + i, 18 + i);
                    }
                }

                // Borde neón
                g2d.setColor(getModel().isRollover() ? LIME_GREEN : BORDER_GREEN);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 18, 18);

                g2d.dispose();
                super.paintComponent(g);
            }
            @Override
            protected void paintBorder(Graphics g) {}
        };
        button.setFont(new Font("Consolas", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }


    // Clase para borde con efecto glow
    private static class GlowBorder extends AbstractBorder {
        private Color color;
        private int thickness;

        public GlowBorder(Color color, int thickness) {
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x, y, width-1, height-1, 8, 8);

            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }

    // ScrollBar personalizada
    private static class HackerScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = ACCENT_GREEN;
            trackColor = DARKER_BG;
            thumbHighlightColor = LIME_GREEN;
            thumbLightShadowColor = DARK_LIME;
            thumbDarkShadowColor = DARKER_BG;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(thumbColor);
            g2d.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 6, 6);

            g2d.setColor(LIME_GREEN);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width-1, thumbBounds.height-1, 6, 6);

            g2d.dispose();
        }
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
                    if (component instanceof JButton && ((JButton) component).getText().contains("OCULTAR")) {
                        ((JButton) component).setText(">> ARCHIVOS <<");
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

            // Update terminal text con estilo hacker
            terminalWindow.getTerminalArea().setForeground(TERMINAL_GREEN);
            terminalWindow.getTerminalArea().setText(">>> COMPILACIÓN EXITOSA <<<\n" +
                    ">>> Sistema: ONLINE\n" +
                    ">>> Estado: READY TO EXECUTE\n" +
                    ">>> Output: " + outputFilename + "\n" +
                    ">>> Presiona EJECUTAR para continuar...");

            runButton.setEnabled(true);
        }
    }

   private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Verificar que tenemos código LLVM para ejecutar
        if (llvmIRCode == null || llvmIRCode.isEmpty()) {
            terminalWindow.getTerminalArea().setForeground(Color.RED);
            terminalWindow.getTerminalArea().append(">>> ERROR: No hay código compilado para ejecutar\n");
            terminalWindow.getTerminalArea().append(">>> Compila primero el programa\n");
            return;
        }
        
        if (outputFilename == null || outputFilename.isEmpty()) {
            terminalWindow.getTerminalArea().setForeground(Color.RED);
            terminalWindow.getTerminalArea().append(">>> ERROR: No se ha especificado archivo de salida\n");
            return;
        }

        // Run executable con logs de hacker
        terminalWindow.getTerminalArea().setForeground(TERMINAL_GREEN);
        terminalWindow.getTerminalArea().append("\n\n>>> INICIANDO EJECUCIÓN...\n");
        terminalWindow.getTerminalArea().append(">>> Cargando módulos...\n");
        terminalWindow.getTerminalArea().append(">>> Estableciendo conexión...\n");
        terminalWindow.getTerminalArea().append(">>> SISTEMA LISTO\n\n");

        try {
            // Crear una copia de la lista para evitar problemas de modificación concurrente
            List<String> modifiedLLVMCode = new ArrayList<>(llvmIRCode);
            
            // DEBUG: Mostrar información del archivo
            File llFile = new File(outputFilename);
            terminalWindow.getTerminalArea().append(">>> DEBUG: Archivo .ll generado: " + outputFilename + "\n");
            terminalWindow.getTerminalArea().append(">>> DEBUG: Archivo existe: " + llFile.exists() + "\n");
            terminalWindow.getTerminalArea().append(">>> DEBUG: Ruta: " + llFile.getAbsolutePath() + "\n");
            terminalWindow.getTerminalArea().append(">>> DEBUG: Tamaño código LLVM: " + modifiedLLVMCode.size() + " líneas\n");

            // Intentar ejecutar con LLVMOutput real
            boolean executionSuccessful = false;
            String output = "";
            
            try {
                // Verificar si la clase LLVMOutput está disponible
                LLVMOutput llvmOutput = new LLVMOutput();
                
                // DEBUG: Mostrar algunas líneas del código LLVM
                terminalWindow.getTerminalArea().append(">>> DEBUG: Primeras líneas del código LLVM:\n");
                for(int i = 0; i < Math.min(3, modifiedLLVMCode.size()); i++) {
                    terminalWindow.getTerminalArea().append("    " + modifiedLLVMCode.get(i) + "\n");
                }
                terminalWindow.getTerminalArea().append(">>> DEBUG: Ejecutando código LLVM...\n");
                
                output = llvmOutput.run(outputFilename, modifiedLLVMCode);
                executionSuccessful = true;
                
            } catch (Exception llvmException) {
                terminalWindow.getTerminalArea().append(">>> ADVERTENCIA: Error con LLVMOutput: " + llvmException.getMessage() + "\n");
                terminalWindow.getTerminalArea().append(">>> Cambiando a modo simulación...\n");
            }
            
            // Si la ejecución real falla, usar simulación
            if (!executionSuccessful || output == null || output.trim().isEmpty() || 
                output.contains("The system cannot find the path specified")) {
                
                terminalWindow.getTerminalArea().append(">>> MODO SIMULACIÓN ACTIVADO\n");
                LLVMOutput mockOutput = new LLVMOutput();
                output = mockOutput.run(outputFilename, modifiedLLVMCode);
            }

            // Mostrar la salida
            terminalWindow.getTerminalArea().append(">>> SALIDA DEL PROGRAMA:\n");
            terminalWindow.getTerminalArea().append("" + "=".repeat(40) + "\n");
            terminalWindow.getTerminalArea().setForeground(Color.WHITE);
            
            if (output != null && !output.trim().isEmpty()) {
                terminalWindow.getTerminalArea().append(output);
            } else {
                terminalWindow.getTerminalArea().append(">>> El programa se ejecutó sin generar salida visible\n");
            }
            
            terminalWindow.getTerminalArea().setForeground(TERMINAL_GREEN);
            terminalWindow.getTerminalArea().append("\n" + "=".repeat(40) + "\n");
            terminalWindow.getTerminalArea().append(">>> EJECUCIÓN COMPLETADA\n");
            terminalWindow.getTerminalArea().append(">>> Sistema en standby...\n");
            
        } catch (Exception e) {
            // Manejo de errores más robusto
            terminalWindow.getTerminalArea().setForeground(Color.RED);
            terminalWindow.getTerminalArea().append(">>> ERROR CRÍTICO EN EJECUCIÓN:\n");
            terminalWindow.getTerminalArea().append(">>> " + e.getClass().getSimpleName() + ": " + e.getMessage() + "\n");
            
            // Intentar con simulación como último recurso
            try {
                terminalWindow.getTerminalArea().setForeground(TERMINAL_GREEN);
                terminalWindow.getTerminalArea().append(">>> Intentando ejecución simulada...\n");
                
                LLVMOutput mockOutput = new LLVMOutput();
                String simulatedOutput = mockOutput.run(outputFilename, llvmIRCode);
                
                terminalWindow.getTerminalArea().append(">>> SALIDA SIMULADA:\n");
                terminalWindow.getTerminalArea().append("" + "=".repeat(40) + "\n");
                terminalWindow.getTerminalArea().setForeground(Color.WHITE);
                terminalWindow.getTerminalArea().append(simulatedOutput);
                terminalWindow.getTerminalArea().setForeground(TERMINAL_GREEN);
                terminalWindow.getTerminalArea().append("\n" + "=".repeat(40) + "\n");
                terminalWindow.getTerminalArea().append(">>> SIMULACIÓN COMPLETADA\n");
                
            } catch (Exception e2) {
                terminalWindow.getTerminalArea().setForeground(Color.RED);
                terminalWindow.getTerminalArea().append(">>> ERROR FATAL: No se pudo ejecutar ni simular\n");
                terminalWindow.getTerminalArea().append(">>> " + e2.getMessage() + "\n");
                e2.printStackTrace(); // Para debugging
            }
        }
    }
}
