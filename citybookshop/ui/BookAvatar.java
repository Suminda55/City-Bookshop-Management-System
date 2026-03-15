package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.net.URL;

public class BookAvatar extends JComponent {

    private final Image image;

    public BookAvatar(String resourcePath, int size) {
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));

        URL url = getClass().getResource(resourcePath);
        if (url != null) {
            image = new ImageIcon(url).getImage();
        } else {
            // If image not found, use null and draw a fallback icon
            image = null;
        }
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Shadow
        g2.setColor(new Color(0, 0, 0, 45));
        g2.fillOval(4, 6, w - 8, h - 8);

        // Circle clip
        Shape circle = new Ellipse2D.Double(0, 0, w - 8, h - 8);
        g2.setClip(circle);

        // Background circle
        g2.setColor(Color.WHITE);
        g2.fill(circle);

        // Draw image (fit)
        if (image != null) {
            g2.drawImage(image, 0, 0, w - 8, h - 8, this);
        } else {
            // Fallback: draw simple book icon
            g2.setClip(null);
            g2.setColor(Color.WHITE);
            g2.fillOval(0, 0, w - 8, h - 8);
            g2.setColor(new Color(0, 123, 255));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2.drawString("📘", (w / 2) - 12, (h / 2) + 8);
        }

        // Border ring
        g2.setClip(null);
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(230, 235, 245));
        g2.drawOval(0, 0, w - 8, h - 8);

        g2.dispose();
    }
}