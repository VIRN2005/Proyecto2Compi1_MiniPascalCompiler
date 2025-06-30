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
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import static org.antlr.v4.runtime.CharStreams.fromFileName;
import static org.main.Main.underline2;

public class Gui extends JFrame {

    // Color Scheme - Epic Dark Theme with Green Accents
    private static final Color BACKGROUND_DARK = new Color(18, 18, 18);
    private static final Color BACKGROUND_MEDIUM = new Color(30, 30, 30);
    private static final Color BACKGROUND_LIGHT = new Color(45, 45, 45);
    private static final Color ACCENT_GREEN = new Color(0, 255, 127);
    private static final Color ACCENT_GREEN_DARK = new Color(0, 200, 100);
    private static final Color TEXT_PRIMARY = new Color(230, 230, 230);
    private static final Color TEXT_SECONDARY = new Color(180, 180, 180);
    private static final Color BORDER_COLOR = new Color(60, 60, 60);
    private static final Color SELECTION_COLOR = new Color(0, 255, 127, 30);
    private static final Color ERROR_COLOR = new Color(255, 85, 85);
    private static final Color SUCCESS_COLOR = new Color(85, 255, 85);

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
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private Timer animationTimer;

    public Gui() {
        super("⚡ MiniPascal Compiler Studio");
        setupLookAndFeel();
        initComponents();
        setupAnimations();
        setPreferredSize(new Dimension(1200, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);

        // Create and show terminal window with epic styling
        terminalWindow = new TerminalWindow();
        styleTerminalWindow();
        terminalWindow.setLocation(getX() + getWidth() + 10, getY());
        terminalWindow.setVisible(true);
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Custom UI defaults for dark theme
            UIManager.put("Panel.background", BACKGROUND_DARK);
            UIManager.put("Button.background", BACKGROUND_MEDIUM);
            UIManager.put("Button.foreground", TEXT_PRIMARY);
            UIManager.put("Tree.background", BACKGROUND_MEDIUM);
            UIManager.put("Tree.foreground", TEXT_PRIMARY);
            UIManager.put("TextArea.background", BACKGROUND_MEDIUM);
            UIManager.put("TextArea.foreground", TEXT_PRIMARY);
            UIManager.put("ScrollPane.background", BACKGROUND_MEDIUM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void styleTerminalWindow() {
        if (terminalWindow != null) {
            terminalWindow.getContentPane().setBackground(BACKGROUND_DARK);
            terminalWindow.getTerminalArea().setBackground(BACKGROUND_DARK);
            terminalWindow.getTerminalArea().setForeground(ACCENT_GREEN);
            terminalWindow.getTerminalArea().setCaretColor(ACCENT_GREEN);
            terminalWindow.getTerminalArea().setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        }
    }

    private void setupAnimations() {
        // Subtle breathing animation for accent elements
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            private float alpha = 0.3f;
            private boolean increasing = true;

            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (increasing) {
                        alpha += 0.02f;
                        if (alpha >= 0.8f) increasing = false;
                    } else {
                        alpha -= 0.02f;
                        if (alpha <= 0.3f) increasing = true;
                    }
                    repaint();
                });
            }
        }, 0, 50);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Gui::new);
    }

    public void initComponents(){
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new RoundedBorder(BORDER_COLOR, 2, 15));
        mainPanel.setBackground(BACKGROUND_DARK);

        // Epic header with gradient effect
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Code area with syntax highlighting feel
        codeArea = createCodeArea();
        JScrollPane codeScrollPane = createStyledScrollPane(codeArea);

        // Create center panel with glow effect
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_DARK);
        centerPanel.setBorder(new GlowBorder(ACCENT_GREEN, 1));

        // Status bar
        JPanel statusPanel = createStatusPanel();
        centerPanel.add(statusPanel, BorderLayout.SOUTH);
        centerPanel.add(codeScrollPane, BorderLayout.CENTER);

        // Epic file explorer
        createFileExplorer();

        // Button panel with hover effects
        createButtonPanel();

        // Add components to main panel
        mainPanel.add(treePanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, BACKGROUND_MEDIUM,
                        getWidth(), 0, BACKGROUND_LIGHT
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Accent line
                g2d.setColor(ACCENT_GREEN);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(20, getHeight() - 3, getWidth() - 20, getHeight() - 3);

                g2d.dispose();
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());

        // Title with epic styling
        JLabel titleLabel = new JLabel("⚡ MINIPASCAL COMPILER STUDIO", JLabel.CENTER);
        titleLabel.setFont(new Font("Orbitron", Font.BOLD, 28));
        titleLabel.setForeground(ACCENT_GREEN);

        JLabel subtitleLabel = new JLabel("Advanced Compilation Environment", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JTextArea createCodeArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        area.setBackground(BACKGROUND_MEDIUM);
        area.setForeground(TEXT_PRIMARY);
        area.setCaretColor(ACCENT_GREEN);
        area.setSelectionColor(SELECTION_COLOR);
        area.setEditable(false);
        area.setLineWrap(false);
        area.setTabSize(4);

        // Add line numbers feel
        area.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 1, 8),
                BorderFactory.createEmptyBorder(10, 15, 10, 10)
        ));

        return area;
    }

    private JScrollPane createStyledScrollPane(JComponent component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_MEDIUM);
        scrollPane.getViewport().setBackground(BACKGROUND_MEDIUM);

        // Custom scrollbar
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        return scrollPane;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(BACKGROUND_LIGHT);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        statusLabel = new JLabel("Ready to compile");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);

        progressBar = new JProgressBar();
        progressBar.setBackground(BACKGROUND_MEDIUM);
        progressBar.setForeground(ACCENT_GREEN);
        progressBar.setBorderPainted(false);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(200, 6));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.EAST);

        return statusPanel;
    }

    private void createFileExplorer() {
        // File tree with epic styling
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("📁 Programs");
        File programsDir = new File("PascalCompiler/Programs");
        createFileTree(programsDir, root);

        tree = new JTree(root);
        tree.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tree.setBackground(BACKGROUND_MEDIUM);
        tree.setForeground(TEXT_PRIMARY);
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);

        // Custom tree renderer
        tree.setCellRenderer(new EpicTreeCellRenderer());

        // Selection listener
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null && node.isLeaf()) {
                selectedFileLabel.setText("📄 " + node.toString());
                selectedFileLabel.setForeground(ACCENT_GREEN);
            } else {
                selectedFileLabel.setText("No file selected");
                selectedFileLabel.setForeground(TEXT_SECONDARY);
            }
        });

        JScrollPane treeScrollPane = createStyledScrollPane(tree);
        treeScrollPane.setPreferredSize(new Dimension(280, 400));

        // Toggle button with modern styling
        JButton toggleTreeButton = createStyledButton("📁 Files", true);
        toggleTreeButton.addActionListener(e -> {
            isTreeExpanded = !isTreeExpanded;
            treeScrollPane.setVisible(isTreeExpanded);
            toggleTreeButton.setText(isTreeExpanded ? "📂 Files" : "📁 Files");
            animateButton(toggleTreeButton);
            treePanel.revalidate();
            treePanel.repaint();
        });

        // Selected file label
        selectedFileLabel = new JLabel("No file selected");
        selectedFileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        selectedFileLabel.setForeground(TEXT_SECONDARY);
        selectedFileLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        selectedFileLabel.setOpaque(true);
        selectedFileLabel.setBackground(BACKGROUND_LIGHT);

        // Tree panel assembly
        treePanel = new JPanel();
        treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.Y_AXIS));
        treePanel.setBackground(BACKGROUND_DARK);
        treePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        treePanel.add(toggleTreeButton);
        treePanel.add(Box.createVerticalStrut(10));
        treePanel.add(treeScrollPane);
        treeScrollPane.setVisible(false);
        treePanel.add(Box.createVerticalStrut(10));
        treePanel.add(selectedFileLabel);
        treePanel.add(Box.createVerticalStrut(15));
    }

    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(BACKGROUND_DARK);

        // Epic buttons with icons and effects
        openButton = createStyledButton("🔓 OPEN", false);
        openButton.addActionListener(this::openButtonActionPerformed);

        compileButton = createStyledButton("⚙️ COMPILE", false);
        compileButton.addActionListener(this::compileButtonActionPerformed);

        runButton = createStyledButton("🚀 EXECUTE", false);
        runButton.addActionListener(this::runButtonActionPerformed);
        runButton.setEnabled(false);

        // Add buttons with spacing
        buttonPanel.add(openButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(compileButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(runButton);
        buttonPanel.add(Box.createVerticalGlue());

        treePanel.add(buttonPanel);
    }

    private JButton createStyledButton(String text, boolean isToggle) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                if (getModel().isPressed()) {
                    g2d.setColor(ACCENT_GREEN_DARK);
                } else if (getModel().isRollover()) {
                    g2d.setColor(ACCENT_GREEN);
                } else if (!isEnabled()) {
                    g2d.setColor(BACKGROUND_LIGHT);
                } else {
                    g2d.setColor(BACKGROUND_MEDIUM);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Border glow effect when enabled
                if (isEnabled()) {
                    g2d.setColor(getModel().isRollover() ? ACCENT_GREEN : BORDER_COLOR);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                }

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(TEXT_PRIMARY);
        button.setPreferredSize(new Dimension(140, 45));
        button.setMaximumSize(new Dimension(140, 45));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        // Hover animation
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    animateButton(button);
                }
            }
        });

        return button;
    }

    private void animateButton(JButton button) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            private int pulse = 0;

            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (pulse++ < 3) {
                        button.repaint();
                    } else {
                        timer.cancel();
                    }
                });
            }
        }, 0, 100);
    }

    private void updateStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? ERROR_COLOR : SUCCESS_COLOR);

        // Flash effect
        Timer flashTimer = new Timer();
        flashTimer.schedule(new TimerTask() {
            private int flashes = 0;
            private boolean visible = true;

            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setVisible(visible);
                    visible = !visible;
                    if (++flashes > 6) {
                        statusLabel.setVisible(true);
                        flashTimer.cancel();
                    }
                });
            }
        }, 0, 150);
    }

    private void showProgress(boolean show) {
        progressBar.setVisible(show);
        if (show) {
            progressBar.setIndeterminate(true);
        }
    }

    // Custom tree cell renderer
    private class EpicTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            setBackgroundNonSelectionColor(BACKGROUND_MEDIUM);
            setTextNonSelectionColor(TEXT_PRIMARY);
            setBackgroundSelectionColor(SELECTION_COLOR);
            setTextSelectionColor(TEXT_PRIMARY);
            setBorderSelectionColor(ACCENT_GREEN);

            if (leaf) {
                setIcon(createIcon("📄", ACCENT_GREEN));
            } else {
                setIcon(createIcon(expanded ? "📂" : "📁", TEXT_SECONDARY));
            }

            return this;
        }

        private Icon createIcon(String emoji, Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    g.setColor(color);
                    g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                    g.drawString(emoji, x, y + 12);
                }

                @Override
                public int getIconWidth() { return 20; }

                @Override
                public int getIconHeight() { return 16; }
            };
        }
    }

    // Custom border classes
    private static class RoundedBorder extends AbstractBorder {
        private Color color;
        private int thickness;
        private int radius;

        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }
    }

    private static class GlowBorder extends AbstractBorder {
        private Color glowColor;
        private int glowSize;

        public GlowBorder(Color glowColor, int glowSize) {
            this.glowColor = glowColor;
            this.glowSize = glowSize;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < glowSize; i++) {
                g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                        (int) (50 * (1.0 - (double) i / glowSize))));
                g2d.drawRoundRect(x + i, y + i, width - 2 * i - 1, height - 2 * i - 1, 8, 8);
            }
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(glowSize + 2, glowSize + 2, glowSize + 2, glowSize + 2);
        }
    }

    // Modern scrollbar UI
    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = ACCENT_GREEN;
            this.trackColor = BACKGROUND_LIGHT;
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
            g2d.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
                    thumbBounds.width - 4, thumbBounds.height - 4, 5, 5);
            g2d.dispose();
        }
    }

    // Original methods adapted with new styling
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
            updateStatus("Opening file...", false);
            showProgress(true);

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            String selectedNodeName = selectedNode.toString();
            String selectedNodePath = "PascalCompiler/Programs/" + selectedNodeName;
            File file = new File(selectedNodePath);

            try {
                String content = Files.readString(file.toPath());
                codeArea.setText(content);

                // Collapse tree with animation
                isTreeExpanded = false;
                Component[] components = treePanel.getComponents();
                for (Component component : components) {
                    if (component instanceof JScrollPane) {
                        component.setVisible(false);
                    }
                }
                for (Component component : components) {
                    if (component instanceof JButton) {
                        ((JButton) component).setText("📁 Files");
                    }
                }
                treePanel.revalidate();
                treePanel.repaint();

                updateStatus("File opened successfully", false);
                showProgress(false);
            } catch (IOException e) {
                updateStatus("Error opening file", true);
                showProgress(false);
                e.printStackTrace();
            }
        } else {
            updateStatus("Please select a file first", true);
        }
    }

    private void compileButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(tree.getSelectionPath() != null){
            updateStatus("Compiling...", false);
            showProgress(true);
            terminalWindow.getTerminalArea().setText("");

            // Disable buttons during compilation
            compileButton.setEnabled(false);
            runButton.setEnabled(false);

            // Use SwingWorker for background compilation
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    performCompilation();
                    return null;
                }

                @Override
                protected void done() {
                    compileButton.setEnabled(true);
                    showProgress(false);
                }
            };
            worker.execute();
        } else {
            updateStatus("Please select a file first", true);
        }
    }

    private void performCompilation() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        String selectedNodeName = selectedNode.toString();
        String selectedNodePath = "PascalCompiler/Programs/" + selectedNodeName;
        File file = new File(selectedNodePath);
        String input = file.getAbsolutePath();
        CharStream charStream = null;

        // Make lineStream final by declaring it as final and using array wrapper
        final String[] linesArray;

        try{
            // Read lines into array to make it effectively final
            linesArray = Files.lines(file.toPath()).toArray(String[]::new);
            charStream = fromFileName(input);
        }catch (Exception e){
            SwingUtilities.invokeLater(() -> updateStatus("File selection error", true));
            return;
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
            SwingUtilities.invokeLater(() -> {
                terminalWindow.getTerminalArea().setText("");
                terminalWindow.getTerminalArea().setForeground(ERROR_COLOR);
                MyErrorListener.errors.forEach(error -> {
                    terminalWindow.getTerminalArea().append(error.errorMsg() + "\n");
                });
                updateStatus("Compilation failed - Syntax errors", true);
            });
            return;
        }

        // Semantic Analysis
        SymbolTable symbolTable = new SymbolTable();
        SemanticErrorCollector errorCollector = new SemanticErrorCollector();
        DeclarationCollector declarationCollector = new DeclarationCollector(symbolTable, errorCollector);
        declarationCollector.visit(tree);

        if(errorCollector.getErrors().size() > 0){
            SwingUtilities.invokeLater(() -> {
                terminalWindow.getTerminalArea().setText("");
                terminalWindow.getTerminalArea().setForeground(ERROR_COLOR);
                // Now use linesArray which is effectively final
                errorCollector.getErrors().forEach(error -> {
                    String send = error.toString() + "\n";
                    send += linesArray[error.getLine() - 1] + "\n";
                    send += underline2(error.getColumn()) + "\n";
                    terminalWindow.getTerminalArea().append(send+ "\n");
                });
                updateStatus("Compilation failed - Declaration errors", true);
            });
            return;
        }

        SemanticAnalizer semanticAnalizer = new SemanticAnalizer(symbolTable, errorCollector);
        semanticAnalizer.visit(tree);

        if(semanticAnalizer.getErrorCollector().getErrorsCount() > 0){
            SwingUtilities.invokeLater(() -> {
                terminalWindow.getTerminalArea().setText("");
                terminalWindow.getTerminalArea().setForeground(ERROR_COLOR);
                // Use linesArray which is effectively final
                semanticAnalizer.getErrorCollector().getErrors().forEach(error -> {
                    String send = error.toString() + "\n";
                    send += linesArray[error.getLine() - 1] + "\n";
                    send += underline2(error.getColumn()) + "\n";
                    terminalWindow.getTerminalArea().append(send + "\n");
                });
                updateStatus("Compilation failed - Semantic errors", true);

                // Show symbol table window with epic styling
                SwingUtilities.invokeLater(() -> {
                    SymbolTableWindow symbolTableWindow = new SymbolTableWindow(symbolTable);
                    styleWindow(symbolTableWindow);
                    symbolTableWindow.setVisible(true);
                });
            });
            return;
        }

        // TAC Generation and Optimization
        TACInstructionCollector tacInstructionCollector = new TACInstructionCollector();
        TACGenerator tacGenerator = new TACGenerator(tacInstructionCollector, symbolTable);
        tacGenerator.visit(tree);
        tacGenerator.printTACInstructions();

        TACOptimizer optimizer = new TACOptimizer(tacInstructionCollector.getInstructions());
        List<TACInstruction> optimizedInstructions = optimizer.optimize();
        for(TACInstruction instruction : optimizedInstructions){
            System.out.println(instruction.toString());
        }

        // LLVM Translation
        LLVMTranslator llvmTranslator = new LLVMTranslator();
        LLVMFileHandler fileHandler = new LLVMFileHandler();

        llvmIRCode = llvmTranslator.translateAll(optimizedInstructions);

        outputFilename = input.substring(0, input.lastIndexOf('.')) + ".ll";
        fileHandler.writeToFile(outputFilename, llvmIRCode, input);
        System.out.println("Output file: " + outputFilename);

        // Success! Show all windows with epic styling
        SwingUtilities.invokeLater(() -> {
            SymbolTableWindow symbolTableWindow = new SymbolTableWindow(symbolTable);
            TACWindow tacWindow = new TACWindow(tacInstructionCollector, optimizedInstructions);
            LLVMWindow llvmWindow = new LLVMWindow(llvmIRCode);

            // Style all windows
            styleWindow(symbolTableWindow);
            styleWindow(tacWindow);
            styleWindow(llvmWindow);

            symbolTableWindow.setVisible(true);
            tacWindow.setVisible(true);
            llvmWindow.setVisible(true);

            // Epic success message
            terminalWindow.getTerminalArea().setForeground(SUCCESS_COLOR);
            terminalWindow.getTerminalArea().setText("✅ COMPILATION SUCCESSFUL!\n");
            terminalWindow.getTerminalArea().append("🎯 Output file: " + outputFilename + "\n");
            terminalWindow.getTerminalArea().append("⚡ Ready for execution!\n");

            updateStatus("Compilation successful! Ready to execute", false);
            runButton.setEnabled(true);

            // Flash run button to indicate it's ready
            flashButton(runButton);
        });
    }

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
        updateStatus("Executing program...", false);
        showProgress(true);

        // Disable run button during execution
        runButton.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                performExecution();
                return null;
            }

            @Override
            protected void done() {
                runButton.setEnabled(true);
                showProgress(false);
            }
        };
        worker.execute();
    }

    private void performExecution() {
        // TODO: Clean up LLVM code (original logic)
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

        SwingUtilities.invokeLater(() -> {
            terminalWindow.getTerminalArea().setForeground(ACCENT_GREEN);
            terminalWindow.getTerminalArea().setText("🚀 PROGRAM EXECUTION:\n");
            terminalWindow.getTerminalArea().append("=" + "=".repeat(50) + "\n");
            terminalWindow.getTerminalArea().append(output);
            terminalWindow.getTerminalArea().append("\n" + "=".repeat(52) + "\n");
            terminalWindow.getTerminalArea().append("✨ Execution completed successfully!");

            updateStatus("Program executed successfully!", false);
        });
    }

    private void styleWindow(JFrame window) {
        if (window != null) {
            window.getContentPane().setBackground(BACKGROUND_DARK);
            // Add more styling as needed for each specific window type
        }
    }

    private void flashButton(JButton button) {
        Timer flashTimer = new Timer();
        flashTimer.schedule(new TimerTask() {
            private int flashes = 0;
            private boolean glowing = false;

            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (glowing) {
                        button.setBackground(ACCENT_GREEN);
                    } else {
                        button.setBackground(BACKGROUND_MEDIUM);
                    }
                    glowing = !glowing;
                    button.repaint();

                    if (++flashes > 8) {
                        button.setBackground(BACKGROUND_MEDIUM);
                        button.repaint();
                        flashTimer.cancel();
                    }
                });
            }
        }, 0, 200);
    }

    @Override
    public void dispose() {
        if (animationTimer != null) {
            animationTimer.cancel();
        }
        super.dispose();
    }
}