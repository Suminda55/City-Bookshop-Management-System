package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {

    private final Color normalColor = new Color(0, 123, 255);
    private final Color hoverColor  = new Color(0, 102, 204);
    private boolean hovered = false;

    public RoundedButton(String text) {
        super(text);

        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Fixed premium size (so it stays centered)
        setPreferredSize(new Dimension(180, 45));
        setMaximumSize(new Dimension(180, 45));
        setMinimumSize(new Dimension(180, 45));

        // Hover animation
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Shadow
        g2.setColor(new Color(0, 0, 0, 45));
        g2.fillRoundRect(4, 4, w - 8, h - 8, 28, 28);

        // Button Fill
        g2.setColor(hovered ? hoverColor : normalColor);
        g2.fillRoundRect(0, 0, w - 8, h - 8, 28, 28);

        g2.dispose();
        super.paintComponent(g);
    }
}
