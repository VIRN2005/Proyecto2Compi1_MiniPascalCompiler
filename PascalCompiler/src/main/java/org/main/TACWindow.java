package org.main;

import IntermediateRepresentation.TACInstruction;
import IntermediateRepresentation.TACInstructionCollector;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class TACWindow extends JFrame {
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
    private static final Color ORANGE_ACCENT = new Color(255, 165, 0);    // Naranja para optimizado

    private JTable TAC;
    private JTable optimizedTACTable;
    private TACInstructionCollector tacInstructionCollector;
    private List<TACInstruction> optimizedTacCode;

    public TACWindow(TACInstructionCollector tacInstructionCollector, List<TACInstruction> optimizedTacCode) {
        this.tacInstructionCollector = tacInstructionCollector;
        this.optimizedTacCode = optimizedTacCode;
        initComponents();
        setTitle("◉ THREE ADDRESS CODE ANALYZER ◉");
        setPreferredSize(new Dimension(1000, 800));
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

        // Título principal con efecto neón animado
        JLabel mainTitle = new JLabel("THREE ADDRESS CODE ANALYZER") {
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
                g2d.setFont(new Font("Consolas", Font.BOLD, 22));
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
        mainTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainTitle.setOpaque(false);
        mainTitle.setPreferredSize(new Dimension(0, 70));

        // Panel para las dos tablas
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        tablesPanel.setOpaque(false);

        // Panel TAC Original
        JPanel tacPanel = createTACPanel("ORIGINAL TAC CODE", TAC = createStyledTable(), 
                                        getTACTableModel(tacInstructionCollector.getInstructions()), 
                                        TEXT_GREEN, LIME_GREEN);

        // Panel TAC Optimizado
        JPanel optimizedTacPanel = createTACPanel("OPTIMIZED TAC CODE", optimizedTACTable = createStyledTable(), 
                                                 getTACTableModel(optimizedTacCode), 
                                                 ORANGE_ACCENT, new Color(255, 140, 0));

        tablesPanel.add(tacPanel);
        tablesPanel.add(optimizedTacPanel);

        // Panel de información
        JPanel infoPanel = createInfoPanel();

        mainPanel.add(mainTitle, BorderLayout.NORTH);
        mainPanel.add(tablesPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        add(mainPanel);
        getContentPane().setBackground(DARK_BG);
    }

    private JPanel createTACPanel(String title, JTable table, DefaultTableModel model, Color primaryColor, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // Título del panel
        JLabel titleLabel = new JLabel(title) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Fondo semi-transparente
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRoundRect(10, 5, getWidth() - 20, getHeight() - 10, 10, 10);

                // Borde brillante
                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(10, 5, getWidth() - 21, getHeight() - 11, 10, 10);

                // Texto con glow
                g2d.setFont(new Font("Consolas", Font.BOLD, 16));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;

                // Efecto glow
                for (int i = 3; i > 0; i--) {
                    g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                    g2d.drawString(getText(), x - i, y);
                    g2d.drawString(getText(), x + i, y);
                    g2d.drawString(getText(), x, y - i);
                    g2d.drawString(getText(), x, y + i);
                }

                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setPreferredSize(new Dimension(0, 40));

        // Configurar tabla
        table.setModel(model);
        configureTable(table, primaryColor, accentColor);

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(table) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(DARK_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        scrollPane.setBorder(new GlowBorder(accentColor, 2));
        scrollPane.getViewport().setBackground(DARKER_BG);
        scrollPane.getVerticalScrollBar().setUI(new HackerScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new HackerScrollBarUI());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTable createStyledTable() {
        return new JTable() {
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
            }
        };
    }

    private void configureTable(JTable table, Color primaryColor, Color accentColor) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setBackground(DARKER_BG);
        table.setForeground(primaryColor);
        table.setSelectionBackground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 50));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(accentColor);
        table.setFont(new Font("Consolas", Font.PLAIN, 11));
        table.setRowHeight(22);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Renderer personalizado para las celdas
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    setBackground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 50));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(DARKER_BG);
                    setForeground(primaryColor);
                }
                
                setOpaque(true);
                setFont(new Font("Consolas", Font.PLAIN, 11));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, accentColor));
                
                return c;
            }
        };

        // Aplicar renderer a todas las columnas
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Header personalizado
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setBackground(DARK_BG);
                setForeground(accentColor);
                setFont(new Font("Consolas", Font.BOLD, 12));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, accentColor));
                setOpaque(true);
                
                return c;
            }
        });
        header.setPreferredSize(new Dimension(0, 30));
    }

    private JPanel createInfoPanel() {
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

        int originalCount = tacInstructionCollector.getInstructions().size();
        int optimizedCount = optimizedTacCode.size();
        int reduction = originalCount - optimizedCount;
        double percentage = originalCount > 0 ? (double)reduction / originalCount * 100 : 0;

        JLabel infoLabel = new JLabel(String.format(
            ">>> Original: %d instrucciones | Optimizado: %d instrucciones | Reducción: %d (%.1f%%) | Estado: ANÁLISIS COMPLETO", 
            originalCount, optimizedCount, reduction, percentage));
        infoLabel.setFont(new Font("Courier New", Font.BOLD, 11));
        infoLabel.setForeground(TEXT_GREEN);
        infoPanel.add(infoLabel);

        return infoPanel;
    }

    private DefaultTableModel getTACTableModel(List<TACInstruction> instructions) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("RESULT");
        model.addColumn("OPERATOR");
        model.addColumn("OPERAND 1");
        model.addColumn("OPERAND 2");
        
        for (TACInstruction instruction : instructions) {
            String result = instruction.getResult() == null ? "NULL" : instruction.getResult().toString();
            String op1 = instruction.getOp1() == null ? "NULL" : instruction.getOp1().toString();
            String op2 = instruction.getOp2() == null ? "NULL" : instruction.getOp2().toString();
            String operator = instruction.getOperation() == null ? "NULL" : instruction.getOperation().toString();
            model.addRow(new Object[]{result, operator, op1, op2});
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