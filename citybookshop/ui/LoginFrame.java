package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    // Demo accounts (KEEPING SAME AS YOUR ZIP)
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";
    private static final String CASHIER_USER = "cashier";
    private static final String CASHIER_PASS = "cash123";

    // ===== Theme (White + Blue) =====
    private static final Color APP_BG = new Color(246, 248, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(220, 226, 235);
    private static final Color TEXT_DARK = new Color(20, 26, 36);
    private static final Color TEXT_MUTED = new Color(110, 120, 135);
    private static final Color PRIMARY_BLUE = new Color(0, 102, 255);

    public LoginFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("CityBookshop - Login");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(APP_BG);
        setContentPane(root);

        // Card wrapper (rounded + shadow)
        JPanel cardShadow = new ShadowRoundPanel(26);
        cardShadow.setOpaque(false);
        cardShadow.setPreferredSize(new Dimension(980, 560));
        cardShadow.setLayout(new BorderLayout());
        root.add(cardShadow);

        // Actual card
        JPanel card = new RoundPanel(26, CARD_BG);
        card.setLayout(new BorderLayout());
        cardShadow.add(card, BorderLayout.CENTER);

        // Top bar
        card.add(buildTopBar(), BorderLayout.NORTH);

        // Main area (Left form + Right image)
        JPanel main = new JPanel(new GridLayout(1, 2, 30, 0));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(30, 40, 30, 40));
        main.add(buildLeftForm());
        main.add(buildRightImage());

        card.add(main, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(18, 26, 10, 26));

        JLabel brand = new JLabel("CityBookshop");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brand.setForeground(PRIMARY_BLUE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JLabel dont = new JLabel("Don't have an account?");
        dont.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dont.setForeground(TEXT_MUTED);

        JButton signUp = new JButton("Sign Up");
        // ✅ NOW SIGN UP BACKGROUND BLUE
        stylePrimaryButtonSmall(signUp);
        signUp.setPreferredSize(new Dimension(110, 36));

        signUp.addActionListener(e -> JOptionPane.showMessageDialog(
                this, "Sign Up page (optional / TODO)", "Info", JOptionPane.INFORMATION_MESSAGE
        ));

        right.add(dont);
        right.add(signUp);

        top.add(brand, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel buildLeftForm() {
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(35, 05, 25, 05));

        JLabel title = new JLabel("Log in");
        title.setFont(new Font("Segoe UI", Font.BOLD, 52));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(title);
        left.add(Box.createRigidArea(new Dimension(0, 35)));

        JLabel uLabel = new JLabel("Username or Email");
        uLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        uLabel.setForeground(Color.BLACK);
        uLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField();
        styleField(usernameField);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(uLabel);
        left.add(Box.createRigidArea(new Dimension(0, 8)));
        left.add(usernameField);
        left.add(Box.createRigidArea(new Dimension(0, 22)));

        JLabel pLabel = new JLabel("Password");
        pLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pLabel.setForeground(Color.BLACK);
        pLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        styleField(passwordField);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(pLabel);
        left.add(Box.createRigidArea(new Dimension(0, 8)));
        left.add(passwordField);
        left.add(Box.createRigidArea(new Dimension(0, 28)));

        JButton loginBtn = new JButton("Log In");
        stylePrimaryButton(loginBtn);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginBtn.setPreferredSize(new Dimension(380, 48));

        left.add(loginBtn);
        left.add(Box.createRigidArea(new Dimension(0, 16)));

        // ✅ FORGOT PASSWORD WILL APPEAR (VISIBLE)
        JLabel forgot = new JLabel("Forgot Password?");
        forgot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgot.setForeground(PRIMARY_BLUE);
        forgot.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(forgot);

        // keep everything visible (no clipping)
        left.add(Box.createVerticalGlue());

        getRootPane().setDefaultButton(loginBtn);
        loginBtn.addActionListener(e -> doLogin());

        // Optional: click on forgot password
        forgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(
                        LoginFrame.this,
                        "Forgot Password (optional / TODO)",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        return left;
    }

    private JPanel buildRightImage() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);

        // Your zip has this image file path:
        // citybookshop/src/images/book.jpg
        ImageIcon icon = new ImageIcon("src/images/book.jpg");

        JLabel imgLabel = new JLabel();
        if (icon.getIconWidth() > 0) {
            Image scaled = icon.getImage().getScaledInstance(430, 430, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } else {
            imgLabel.setText("Image missing: src/images/book.jpg");
            imgLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            imgLabel.setForeground(TEXT_MUTED);
        }

        right.add(imgLabel);
        return right;
    }

    // ========= LOGIN (same logic from your ZIP) =========
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Username and Password!",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (username.equalsIgnoreCase(ADMIN_USER) && password.equals(ADMIN_PASS)) {
            AdminDashboard ad = new AdminDashboard("Admin");
            ad.setVisible(true);
            dispose();
            return;
        }

        if (username.equalsIgnoreCase(CASHIER_USER) && password.equals(CASHIER_PASS)) {
            CashierDashboard cd = new CashierDashboard("Cashier");
            cd.setVisible(true);
            dispose();
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Invalid Username or Password!",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ========= STYLES =========
    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(TEXT_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setPreferredSize(new Dimension(380, 44));
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
    }

    // ✅ Used for Sign Up button (blue background)
    private void stylePrimaryButtonSmall(JButton btn) {
        btn.setBackground(PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btn.setOpaque(true);
    }

    // ========= ROUNDED PANELS =========
    static class RoundPanel extends JPanel {
        private final int arc;
        private final Color bg;

        RoundPanel(int arc, Color bg) {
            this.arc = arc;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class ShadowRoundPanel extends JPanel {
        private final int arc;

        ShadowRoundPanel(int arc) {
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // soft shadow
            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new RoundRectangle2D.Double(10, 12, getWidth() - 20, getHeight() - 20, arc, arc));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}