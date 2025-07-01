package org.main;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TerminalWindow extends JFrame {
    // Colores del tema hacker (mismos que SymbolTableWindow)
    private static final Color DARK_BG = new Color(15, 15, 15);           // Fondo principal ultra oscuro
    private static final Color DARKER_BG = new Color(8, 8, 8);           // Fondo más oscuro
    private static final Color LIME_GREEN = new Color(0, 255, 127);       // Verde lima brillante
    private static final Color DARK_LIME = new Color(0, 200, 100);        // Verde lima oscuro
    private static final Color ACCENT_GREEN = new Color(0, 150, 75);      // Verde acento
    private static final Color TEXT_GREEN = new Color(0, 255, 100);       // Verde texto
    private static final Color BORDER_GREEN = new Color(0, 180, 90);      // Verde bordes
    private static final Color HOVER_GREEN = new Color(0, 120, 60);       // Verde hover
    private static final Color TERMINAL_GREEN = new Color(0, 255, 0);     // Verde terminal clásico
    private static final Color SELECTION_GREEN = new Color(0, 255, 127, 50); // Verde selección

    private JTextArea terminalArea;
    private JPanel mainPanel;

    public TerminalWindow() {
        super("◉ TERMINAL ◉");
        initComponents();
        setPreferredSize(new Dimension(800, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setUndecorated(false);
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Panel principal con gradiente y efectos Matrix
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
        mainPanel.setBackground(DARK_BG);

        // Título con efecto neón animado
        JLabel terminalLabel = new JLabel("TERMINAL ACCESS GRANTED") {
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
                g2d.setFont(new Font("Consolas", Font.BOLD, 20));
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
        terminalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        terminalLabel.setOpaque(false);
        terminalLabel.setPreferredSize(new Dimension(0, 70));

        // Crear terminal con efectos de escaneo
        terminalArea = new JTextArea() {
            private Timer blinkTimer;
            private boolean showCursor = true;
            
            {
                // Cursor parpadeante
                blinkTimer = new Timer(530, e -> {
                    showCursor = !showCursor;
                    repaint();
                });
                blinkTimer.start();
            }
            
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
                for (int i = 0; i < 20; i++) {
                    int x = (int)(Math.random() * getWidth());
                    int y = (int)(Math.random() * getHeight());
                    g2d.fillRect(x, y, 1, 1);
                }

                g2d.dispose();
                super.paintComponent(g);
                
                // Dibujar cursor personalizado
                if (showCursor && isFocusOwner()) {
                    g2d = (Graphics2D) g.create();
                    g2d.setColor(TERMINAL_GREEN);
                    try {
                        int caretPos = getCaretPosition();
                        Rectangle caretRect = modelToView(caretPos);
                        if (caretRect != null) {
                            g2d.fillRect(caretRect.x, caretRect.y, 2, caretRect.height);
                            
                            // Efecto glow del cursor
                            g2d.setColor(new Color(0, 255, 0, 100));
                            g2d.fillRect(caretRect.x - 1, caretRect.y, 4, caretRect.height);
                        }
                    } catch (Exception ex) {
                        // Ignorar errores de posición del cursor
                    }
                    g2d.dispose();
                }
            }
        };

        terminalArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        terminalArea.setEditable(true);
        terminalArea.setLineWrap(true);
        terminalArea.setWrapStyleWord(true);
        terminalArea.setBackground(DARKER_BG);
        terminalArea.setForeground(TERMINAL_GREEN);
        terminalArea.setCaretColor(TERMINAL_GREEN);
        terminalArea.setSelectionColor(SELECTION_GREEN);
        terminalArea.setSelectedTextColor(Color.WHITE);
        terminalArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Texto inicial del terminal
        terminalArea.setText(">>> Sistema inicializado...\n" +
                           ">>> Conexión establecida\n" +
                           ">>> Acceso autorizado\n" +
                           ">>> Terminal listo para comandos\n\n" +
                           "root@hackersys:~$ ");

        // Efecto de tipeo en tiempo real
        terminalArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    SwingUtilities.invokeLater(() -> {
                        terminalArea.append("\nroot@hackersys:~$ ");
                        terminalArea.setCaretPosition(terminalArea.getDocument().getLength());
                    });
                }
            }
        });

        // ScrollPane personalizado
        JScrollPane terminalScrollPane = new JScrollPane(terminalArea) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(DARK_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        terminalScrollPane.setBorder(new GlowBorder(BORDER_GREEN, 2));
        terminalScrollPane.getViewport().setBackground(DARKER_BG);

        // Personalizar scrollbars
        terminalScrollPane.getVerticalScrollBar().setUI(new HackerScrollBarUI());
        terminalScrollPane.getHorizontalScrollBar().setUI(new HackerScrollBarUI());

        // Panel de estado con información del sistema
        JPanel statusPanel = new JPanel() {
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
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setOpaque(false);
        statusPanel.setPreferredSize(new Dimension(0, 45));

        JLabel statusLabel = new JLabel(">>> Estado: CONECTADO | Usuario: root | Sesión: ACTIVA | Protocolo: SSH-256");
        statusLabel.setFont(new Font("Courier New", Font.BOLD, 12));
        statusLabel.setForeground(TEXT_GREEN);
        statusPanel.add(statusLabel);

        // Agregar componentes al panel principal
        mainPanel.add(terminalLabel, BorderLayout.NORTH);
        mainPanel.add(terminalScrollPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
        getContentPane().setBackground(DARK_BG);
    }

    public JTextArea getTerminalArea() {
        return terminalArea;
    }

    // Clase para borde con efecto glow (reutilizada de SymbolTableWindow)
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

    // ScrollBar personalizada con estilo hacker (reutilizada de SymbolTableWindow)
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