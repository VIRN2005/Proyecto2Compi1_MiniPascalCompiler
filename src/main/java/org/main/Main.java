package org.main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Comparator;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import static org.antlr.v4.runtime.CharStreams.fromFileName;

public class Main {
    public static void main(String[] args) {
        // look and feel Nimbus
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si falla, se usa el look and feel por defecto.
        }
        SwingUtilities.invokeLater(() -> {
            CompilerGUI gui = new CompilerGUI();
            gui.setVisible(true);
        });
    }
}

// Panel con fondo degradado
class GradientPanel extends JPanel {
    private final Color color1;
    private final Color color2;

    public GradientPanel(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
        g2d.dispose();
        super.paintComponent(g);
    }
}

class CompilerGUI extends JFrame {
    private final JTextArea outputArea;
    private final JButton openFileButton;
    private final JButton compileButton;
    private File selectedFile;

    public CompilerGUI() {
        setTitle("MiniPascal Compiler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        GradientPanel headerPanel = new GradientPanel(new Color(45, 45, 45), new Color(25, 25, 25));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(950, 100));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("MiniPascal Compiler", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Panel de botones en el header
        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        headerButtons.setOpaque(false);
        openFileButton = new JButton("Abrir Archivo");
        styleButton(openFileButton);
        compileButton = new JButton("Compilar");
        styleButton(compileButton);
        compileButton.setEnabled(false);
        headerButtons.add(openFileButton);
        headerButtons.add(compileButton);
        headerPanel.add(headerButtons, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Área de salida con scroll y style
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(30, 30, 30));
        outputArea.setForeground(new Color(0, 230, 118));  // Verde chillón
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 230, 118)), "Salida del Compilador", 0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(0, 230, 118)));
        add(scrollPane, BorderLayout.CENTER);

        // Eventos de botones
        openFileButton.addActionListener(e -> openFile());
        compileButton.addActionListener(e -> compileFile());
    }

    // Metodo botones
    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(0, 150, 136));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 230, 118), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser("./");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos (.txt, .pas, .mp)", "txt", "pas", "mp");
        fileChooser.setFileFilter(filter);
        int selection = fileChooser.showOpenDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            outputArea.append("Archivo seleccionado: " + selectedFile.getAbsolutePath() + "\n");
            compileButton.setEnabled(true);
        }
    }

    private void compileFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado un archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            // Reiniciamos el área de salida para cada compilación
            outputArea.setText("");
            outputArea.append("Compilando: " + selectedFile.getAbsolutePath() + "\n\n");

            CharStream charStream = fromFileName(selectedFile.getAbsolutePath());
            MiniPascalLexer lexer = new MiniPascalLexer(charStream);
            lexer.removeErrorListeners();
            lexer.addErrorListener(new MyErrorListener(true));
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            MiniPascalParser parser = new MiniPascalParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new MyErrorListener(false));
            ParseTree tree = parser.program_block();

            // Ordenamos y mostramos errores (si existen)
            MyErrorListener.errors.sort(Comparator.comparingInt(MyErrorListener.MyError::line));
            StringBuilder errorsOutput = new StringBuilder();
            MyErrorListener.errors.forEach(error -> errorsOutput.append(error.errorMsg()).append("\n"));

            if (errorsOutput.length() > 0) {
                outputArea.append("❌ ¡Errores de compilación encontrados! ❌\n");
                outputArea.append(errorsOutput.toString() + "\n");
            } else {
                MyVisitor visitor = new MyVisitor();
                String result = visitor.visit(tree);
                outputArea.append("✅ ¡Compilación exitosa! ✅\n\n" + result + "\n");
            }
        } catch (Exception ex) {
            outputArea.append("Excepción durante la compilación: " + ex.getMessage() + "\n");
            ex.printStackTrace();
        }
    }
}
