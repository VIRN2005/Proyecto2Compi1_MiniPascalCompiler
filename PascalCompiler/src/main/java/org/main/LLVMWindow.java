package org.main;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LLVMWindow extends JFrame {
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

    private JTextArea llvmTextArea;
    private List<String> llvmIRCode;

    public LLVMWindow(List<String> llvmIRCode) {
        this.llvmIRCode = llvmIRCode;
        initComponents();
        setTitle("◉ LLVM IR CODE ◉");
        setPreferredSize(new Dimension(900, 700));
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(false);
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Panel principal con gradiente
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)) {
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
                g2d.setColor(new Color(0, 255, 127, 15));
                for (int i = 0; i < getWidth(); i += 120) {
                    g2d.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight(); i += 80) {
                    g2d.drawLine(0, i, getWidth(), i);
                }

                g2d.dispose();
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título con efecto neón animado
        JLabel llvmLabel = new JLabel("LLVM INTERMEDIATE REPRESENTATION") {
            private float glow = 0f;
            private Timer timer;
            {
                timer = new Timer(40, e -> {
                    glow += 0.1f;
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
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.fillRoundRect(15, 8, getWidth() - 30, getHeight() - 16, 15, 15);

                // Glow animado verde
                int glowSize = 12 + (int)(Math.sin(glow) * 4);
                for (int i = glowSize; i > 0; i -= 2) {
                    g2d.setColor(new Color(0, 255, 127, 15));
                    g2d.setStroke(new BasicStroke(i));
                    g2d.drawRoundRect(15 - i/2, 8 - i/2, getWidth() - 30 + i, getHeight() - 16 + i, 15 + i, 15 + i);
                }

                // Texto principal con efecto neón
                g2d.setFont(new Font("Consolas", Font.BOLD, 24));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 3;

                // Sombra verde glow
                for (int i = 4; i > 0; i--) {
                    g2d.setColor(new Color(0, 255, 127, 25));
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
        llvmLabel.setHorizontalAlignment(SwingConstants.CENTER);
        llvmLabel.setOpaque(false);
        llvmLabel.setPreferredSize(new Dimension(0, 80));

        // Área de texto con estilo terminal
        llvmTextArea = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo con efecto scanlines
                g2d.setColor(DARKER_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Líneas de escaneo sutiles
                g2d.setColor(new Color(0, 255, 127, 8));
                for (int y = 0; y < getHeight(); y += 3) {
                    g2d.drawLine(0, y, getWidth(), y);
                }

                // Efecto de ruido digital sutil
                g2d.setColor(new Color(0, 255, 127, 5));
                for (int i = 0; i < 50; i++) {
                    int x = (int)(Math.random() * getWidth());
                    int y = (int)(Math.random() * getHeight());
                    g2d.fillRect(x, y, 1, 1);
                }

                g2d.dispose();
                super.paintComponent(g);
            }
        };
        llvmTextArea.setEditable(false);
        llvmTextArea.setBackground(DARKER_BG);
        llvmTextArea.setForeground(TEXT_GREEN);
        llvmTextArea.setCaretColor(LIME_GREEN);
        llvmTextArea.setSelectionColor(HOVER_GREEN);
        llvmTextArea.setSelectedTextColor(Color.WHITE);
        llvmTextArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        llvmTextArea.setLineWrap(false);
        llvmTextArea.setWrapStyleWord(false);

        // Construir el código LLVM IR con prefijo hacker
        StringBuilder llvmIR = new StringBuilder();
        llvmIR.append("; ================================================\n");
        llvmIR.append("; LLVM IR GENERATED CODE - MINIPASCAL COMPILER\n");
        llvmIR.append("; System Status: ONLINE | Security: BYPASSED\n");
        llvmIR.append("; ================================================\n\n");
        
        for (String line : llvmIRCode) {
            llvmIR.append(line).append("\n");
        }
        
        llvmIR.append("\n; ================================================\n");
        llvmIR.append("; END OF GENERATED CODE | Status: COMPLETE\n");
        llvmIR.append("; ================================================");
        
        llvmTextArea.setText(llvmIR.toString());

        // ScrollPane personalizado
        JScrollPane llvmScrollPane = new JScrollPane(llvmTextArea) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(DARK_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        llvmScrollPane.setBorder(new GlowBorder(BORDER_GREEN, 2));
        llvmScrollPane.getViewport().setBackground(DARKER_BG);

        // Personalizar scrollbars
        llvmScrollPane.getVerticalScrollBar().setUI(new HackerScrollBarUI());
        llvmScrollPane.getHorizontalScrollBar().setUI(new HackerScrollBarUI());

        // Panel de información adicional
        JPanel infoPanel = new JPanel() {
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
            }
        };
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        infoPanel.setPreferredSize(new Dimension(0, 45));

        JLabel infoLabel = new JLabel(">>> Líneas de código: " + llvmIRCode.size() + " | Estado: COMPILADO | Formato: LLVM IR");
        infoLabel.setFont(new Font("Courier New", Font.BOLD, 12));
        infoLabel.setForeground(TEXT_GREEN);
        infoPanel.add(infoLabel);

        // Agregar componentes al panel principal
        mainPanel.add(llvmLabel, BorderLayout.NORTH);
        mainPanel.add(llvmScrollPane, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        add(mainPanel);
        getContentPane().setBackground(DARK_BG);
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

            // Efecto glow múltiple
            for (int i = thickness + 2; i > 0; i--) {
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2d.setStroke(new BasicStroke(i));
                g2d.drawRoundRect(x, y, width-1, height-1, 8, 8);
            }

            // Borde principal
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x, y, width-1, height-1, 8, 8);

            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }
    }

    // ScrollBar personalizada con estilo hacker
    private static class HackerScrollBarUI extends BasicScrollBarUI {
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

            // Fondo del thumb con gradiente
            GradientPaint gradient = new GradientPaint(
                    thumbBounds.x, thumbBounds.y, ACCENT_GREEN,
                    thumbBounds.x + thumbBounds.width, thumbBounds.y + thumbBounds.height, DARK_LIME
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, 
                             thumbBounds.width - 4, thumbBounds.height - 4, 6, 6);

            // Borde brillante
            g2d.setColor(LIME_GREEN);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, 
                             thumbBounds.width - 5, thumbBounds.height - 5, 6, 6);

            g2d.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(DARKER_BG);
            g2d.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

            // Líneas sutiles en el track
            g2d.setColor(new Color(0, 255, 127, 10));
            for (int i = trackBounds.y; i < trackBounds.y + trackBounds.height; i += 4) {
                g2d.drawLine(trackBounds.x, i, trackBounds.x + trackBounds.width, i);
            }

            g2d.dispose();
        }
    }
}