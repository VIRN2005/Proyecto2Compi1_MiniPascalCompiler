package org.main;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.antlr.v4.runtime.CharStream;
import static org.antlr.v4.runtime.CharStreams.fromFileName;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

// ... Otras clases como MyErrorListener, MyVisitor ya estarán en el código base

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            CompilerGUI gui = new CompilerGUI();
            gui.setVisible(true);
        });
    }
}

class GradientPanel extends JPanel {
    private final Color color1, color2;

    public GradientPanel(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(
                0, 0, color1,
                getWidth(), getHeight(), color2
        );

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < getWidth(); i += 2) {
            for (int j = 0; j < getHeight(); j += 2) {
                int alpha = (int) (Math.random() * 20);
                g2d.setColor(new Color(255, 255, 255, alpha));
                g2d.fillRect(i, j, 1, 1);
            }
        }

        g2d.dispose();
        super.paintComponent(g);
    }
}

class RoundedButton extends JButton {
    private static final int ARC_SIZE = 20;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(57, 255, 20), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2d.setColor(new Color(0, 200, 83));
        } else if (getModel().isRollover()) {
            g2d.setColor(new Color(57, 255, 20));
        } else {
            g2d.setColor(new Color(57, 255, 20));
        }

        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), ARC_SIZE, ARC_SIZE));

        if (getModel().isRollover()) {
            g2d.setStroke(new BasicStroke(3f));
            g2d.setColor(new Color(57, 255, 20, 180));
            g2d.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, ARC_SIZE, ARC_SIZE));
        }

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(getText(), x, y);

        g2d.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(150, 50);
    }
}

class CompilerGUI extends JFrame {
    private final JTextArea outputArea;
    private final RoundedButton openFileButton, compileButton;
    private File selectedFile;

    public CompilerGUI() {
        setTitle("MiniPascal Compiler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        GradientPanel headerPanel = new GradientPanel(
                new Color(10, 10, 10),
                new Color(20, 20, 20)
        );
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(1100, 150));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("MiniPascal Compiler", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(new Color(57, 255, 20));

        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        headerButtons.setOpaque(false);
        openFileButton = new RoundedButton("Abrir Archivo");
        compileButton = new RoundedButton("Compilar");
        compileButton.setEnabled(false);
        headerButtons.add(openFileButton);
        headerButtons.add(compileButton);
        headerPanel.add(headerButtons, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Consolas", Font.BOLD, 16));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(10, 10, 10));
        outputArea.setForeground(new Color(57, 255, 20));
        outputArea.setMargin(new Insets(20, 20, 20, 20));
        outputArea.setText("Bienvenido al compilador MiniPascal\nSeleccione un archivo para comenzar...");

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 20, 20, 20),
                BorderFactory.createLineBorder(new Color(57, 255, 20), 3)
        ));
        add(scrollPane, BorderLayout.CENTER);

        openFileButton.addActionListener(e -> openFile());
        compileButton.addActionListener(e -> compileFile());
        openFileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        compileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser("./");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Texto (.txt)", "txt"));
        int selection = fileChooser.showOpenDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            outputArea.setText("Archivo seleccionado: " + selectedFile.getAbsolutePath() + "\n");
            compileButton.setEnabled(true);
        }
    }

    private void compileFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado un archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            outputArea.setText("Compilando: " + selectedFile.getAbsolutePath() + "\n\n");

            CharStream charStream = fromFileName(selectedFile.getAbsolutePath());
            MiniPascalLexer lexer = new MiniPascalLexer(charStream);
            lexer.removeErrorListeners();
            lexer.addErrorListener(new MyErrorListener(true));
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            MiniPascalParser parser = new MiniPascalParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new MyErrorListener(false));
            ParseTree tree = parser.program_block();

            MyErrorListener.errors.sort(Comparator.comparingInt(MyErrorListener.MyError::line));
            StringBuilder errorsOutput = new StringBuilder();
            MyErrorListener.errors.forEach(error -> errorsOutput.append(error.errorMsg()).append("\n"));

            if (errorsOutput.length() > 0) {
                outputArea.append("Errores de compilación encontrados: -- \n" + errorsOutput + "\n");

                String fileContent = new String(Files.readAllBytes(selectedFile.toPath()), StandardCharsets.UTF_8);
                for (MyErrorListener.MyError error : MyErrorListener.errors) {
                    int errorLine = error.line();
                    int lineStart = getLineStartPosition(fileContent, errorLine);
                    int lineEnd = getLineEndPosition(fileContent, errorLine);
                    highlightLine(lineStart, lineEnd, Color.RED);
                }
            } else {
                outputArea.append("¡Compilación exitosa! 🎉\n");

                String successMessage = "🎉 La compilación fue exitosa 🚀\n";
                outputArea.append(successMessage);
                outputArea.setForeground(new Color(0, 255, 128));
                outputArea.setFont(new Font("Segoe UI", Font.BOLD, 18));
                outputArea.append("Generando código intermedio...\n");

                animateSuccessMessage();
            }
        } catch (Exception ex) {
            outputArea.append("Error durante la compilación: -- " + ex.getMessage() + "\n");
        }
    }

    private void highlightLine(int start, int end, Color highlightColor) {
        outputArea.setSelectionStart(start);
        outputArea.setSelectionEnd(end);
        outputArea.setSelectedTextColor(highlightColor);
        outputArea.setSelectionColor(new Color(255, 0, 0, 50));
    }

    private int getLineStartPosition(String content, int line) {
        int lineStart = 0;
        for (int i = 0; i < line - 1; i++) {
            lineStart = content.indexOf("\n", lineStart) + 1;
        }
        return lineStart;
    }

    private int getLineEndPosition(String content, int line) {
        int lineEnd = getLineStartPosition(content, line);
        return content.indexOf("\n", lineEnd);
    }

    private void animateSuccessMessage() {
        Timer timer = new Timer(100, new ActionListener() {
            private boolean toggle = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                String currentText = outputArea.getText();
                if (currentText.contains("🎉 La compilación fue exitosa 🚀")) {
                    outputArea.setText(toggle ? "¡Compilación exitosa! \n\nGenerando código intermedio...\n" : currentText);
                    toggle = !toggle;
                }
            }
        });
        timer.setRepeats(true);
        timer.start();
    }
}
