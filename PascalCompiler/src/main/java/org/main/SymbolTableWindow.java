package org.main;

import Symbols.Symbol;
import Symbols.SymbolTable;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SymbolTableWindow extends JFrame {
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
    private static final Color SELECTION_GREEN = new Color(0, 255, 127, 50); // Verde selección

    private JTable symbolTable;
    private SymbolTable symTable;

    public SymbolTableWindow(SymbolTable symTable) {
        this.symTable = symTable;
        initComponents();
        setTitle("◉ SYMBOL TABLE ◉");
        setPreferredSize(new Dimension(800, 600));
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
        JLabel symbolTableLabel = new JLabel("SYMBOL TABLE DATABASE") {
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
        symbolTableLabel.setHorizontalAlignment(SwingConstants.CENTER);
        symbolTableLabel.setOpaque(false);
        symbolTableLabel.setPreferredSize(new Dimension(0, 80));

        // Crear tabla personalizada con estilo hacker
        symbolTable = new JTable() {
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
                for (int i = 0; i < 30; i++) {
                    int x = (int)(Math.random() * getWidth());
                    int y = (int)(Math.random() * getHeight());
                    g2d.fillRect(x, y, 1, 1);
                }

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        // Configurar el modelo de la tabla
        symbolTable.setModel(getSymbolTableModel());
        symbolTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Estilo de la tabla
        symbolTable.setBackground(DARKER_BG);
        symbolTable.setForeground(TEXT_GREEN);
        symbolTable.setSelectionBackground(SELECTION_GREEN);
        symbolTable.setSelectionForeground(Color.WHITE);
        symbolTable.setGridColor(BORDER_GREEN);
        symbolTable.setFont(new Font("Consolas", Font.PLAIN, 12));
        symbolTable.setRowHeight(25);
        symbolTable.setShowGrid(true);
        symbolTable.setIntercellSpacing(new Dimension(1, 1));

        // Renderer personalizado para las celdas
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    setBackground(SELECTION_GREEN);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(DARKER_BG);
                    setForeground(TEXT_GREEN);
                }
                
                setOpaque(true);
                setFont(new Font("Consolas", Font.PLAIN, 12));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_GREEN));
                
                return c;
            }
        };

        // Aplicar renderer a todas las columnas
        for (int i = 0; i < symbolTable.getColumnModel().getColumnCount(); i++) {
            symbolTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Header personalizado
        JTableHeader header = symbolTable.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setBackground(DARK_BG);
                setForeground(LIME_GREEN);
                setFont(new Font("Consolas", Font.BOLD, 14));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, BORDER_GREEN));
                setOpaque(true);
                
                return c;
            }
        });
        header.setPreferredSize(new Dimension(0, 35));

        // ScrollPane personalizado
        JScrollPane symbolTableScrollPane = new JScrollPane(symbolTable) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(DARK_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        symbolTableScrollPane.setBorder(new GlowBorder(BORDER_GREEN, 2));
        symbolTableScrollPane.getViewport().setBackground(DARKER_BG);

        // Personalizar scrollbars
        symbolTableScrollPane.getVerticalScrollBar().setUI(new HackerScrollBarUI());
        symbolTableScrollPane.getHorizontalScrollBar().setUI(new HackerScrollBarUI());

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

        int symbolCount = symTable.getSymbols().size();
        JLabel infoLabel = new JLabel(">>> Símbolos registrados: " + symbolCount + " | Estado: ACCESO COMPLETO | Base de datos: ACTIVA");
        infoLabel.setFont(new Font("Courier New", Font.BOLD, 12));
        infoLabel.setForeground(TEXT_GREEN);
        infoPanel.add(infoLabel);

        // Agregar componentes al panel principal
        mainPanel.add(symbolTableLabel, BorderLayout.NORTH);
        mainPanel.add(symbolTableScrollPane, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        add(mainPanel);
        getContentPane().setBackground(DARK_BG);
    }

    private DefaultTableModel getSymbolTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("IDENTIFIER");
        model.addColumn("TYPE");
        model.addColumn("SCOPE LEVEL");
        
        // Añadir información a la tabla desde el mapa de la tabla de símbolos
        for (Symbol symbol : symTable.getSymbols().values()) {
            model.addRow(new Object[]{
                symbol.getIdentifier(), 
                symbol.getType(), 
                symbol.getScopeLevel()
            });
        }
        return model;
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