package ui;

import model.*;
import service.DataStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CashierDashboard extends JFrame {

    // ===== Layout =====
    private JPanel sidebar;
    private boolean sidebarVisible = true;
    private JPanel contentPanel;

    // ===== Data =====
    private ArrayList<Book> books;
    private ArrayList<Category> categories;

    private static final String BOOKS_FILE = "books.dat";
    private static final String CATEGORIES_FILE = "categories.dat";

    // ===== Table =====
    private JTable booksTable;
    private DefaultTableModel booksModel;
    private TableRowSorter<DefaultTableModel> booksSorter;

    // ===== Theme (same as your AdminDashboard) =====
    private static final Color BG = new Color(245, 247, 250);
    private static final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private static final Color PRIMARY_BLUE_HOVER = new Color(0, 102, 204);
    private static final Color GREEN_COLOR = new Color(40, 167, 69);

    public CashierDashboard(String role) {
        setTitle("Cashier Dashboard");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        books = loadList(BOOKS_FILE);
        categories = loadList(CATEGORIES_FILE);

        createHeader();
        createSidebar();
        createContent();

        showBooksPage(); // default
    }

    // ================= HEADER =================
    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK),
                new EmptyBorder(15, 20, 15, 20)
        ));

        // Hamburger icon
        JLabel hamburger = new JLabel("☰");
        hamburger.setFont(new Font("Segoe UI", Font.BOLD, 22));
        hamburger.setForeground(Color.BLACK);
        hamburger.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hamburger.setBorder(new EmptyBorder(0, 0, 0, 8));

        hamburger.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                toggleSidebar();
            }
        });

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(hamburger);
        left.add(title);

        JLabel logout = new JLabel("Logout");
        logout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logout.setForeground(PRIMARY_BLUE);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                logout.setForeground(PRIMARY_BLUE_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                logout.setForeground(PRIMARY_BLUE);
            }
        });

        header.add(left, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    // ================= SIDEBAR =================
    private void createSidebar() {
        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(28, 35, 43));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(12, 10, 12, 10));

        JLabel brand = new JLabel("  CITY BOOKSHOP");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brand.setBorder(new EmptyBorder(10, 10, 16, 10));
        sidebar.add(brand);

        // ✅ Only two options for cashier
        sidebar.add(sideBtn("Books Management", this::showBooksPage));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(sideBtn("View Book Details", this::showViewDetailsPage));

        sidebar.add(Box.createVerticalGlue());

        add(sidebar, BorderLayout.WEST);
    }

    private JButton sideBtn(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setBackground(new Color(28, 35, 43));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(new EmptyBorder(12, 14, 12, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(42, 53, 66)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(new Color(28, 35, 43)); }
        });

        btn.addActionListener(e -> action.run());
        return btn;
    }

    // ================= CONTENT =================
    private void createContent() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG);
        add(contentPanel, BorderLayout.CENTER);
    }

    // ================= SIDEBAR TOGGLE =================
    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        sidebar.setVisible(sidebarVisible);
        revalidate();
        repaint();
    }

    // ============================================================
    // ===================== BOOKS PAGE (CASHIER) ==================
    // ============================================================
    private void showBooksPage() {
        contentPanel.removeAll();

        JPanel top = buildBooksSearchPanel();
        JScrollPane center = buildBooksTablePanel();

        contentPanel.add(top, BorderLayout.NORTH);
        contentPanel.add(center, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ✅ Same search UI as admin (Search + Category + Search + Clear)
    private JPanel buildBooksSearchPanel() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel sLabel = new JLabel("Search:");
        sLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JTextField searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(240, 34));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel cLabel = new JLabel("Category:");
        cLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JComboBox<String> categoryBox = new JComboBox<>();
        categoryBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryBox.setPreferredSize(new Dimension(180, 34));
        categoryBox.setBackground(Color.WHITE);

        categoryBox.addItem("All");
        if (categories != null) {
            for (Category c : categories) {
                if (c != null && c.getName() != null && !c.getName().trim().isEmpty()) {
                    categoryBox.addItem(c.getName().trim());
                }
            }
        }

        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");

        styleBlueButton(searchBtn);
        styleGreenButton(clearBtn);

        Runnable apply = () -> {
            if (booksSorter == null) return;

            String q = searchField.getText().trim();
            String cat = categoryBox.getSelectedItem() == null ? "All" : categoryBox.getSelectedItem().toString();

            ArrayList<RowFilter<Object, Object>> filters = new ArrayList<>();

            if (!q.isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(q)));
            }

            if (!"All".equals(cat)) {
                // Category column index = 3
                filters.add(RowFilter.regexFilter("^" + Pattern.quote(cat) + "$", 3));
            }

            if (filters.isEmpty()) booksSorter.setRowFilter(null);
            else if (filters.size() == 1) booksSorter.setRowFilter(filters.get(0));
            else booksSorter.setRowFilter(RowFilter.andFilter(filters));
        };

        searchBtn.addActionListener(e -> apply.run());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            categoryBox.setSelectedIndex(0);
            if (booksSorter != null) booksSorter.setRowFilter(null);
        });

        top.add(sLabel);
        top.add(searchField);
        top.add(cLabel);
        top.add(categoryBox);
        top.add(searchBtn);
        top.add(clearBtn);

        return top;
    }

    private JScrollPane buildBooksTablePanel() {
        booksModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Category", "Price"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        booksTable = new JTable(booksModel);
        styleTable(booksTable);

        booksSorter = new TableRowSorter<>(booksModel);
        booksTable.setRowSorter(booksSorter);

        refreshBooksTable();

        centerAlignAllColumns(booksTable);
        centerAlignHeader(booksTable);

        // Double click row -> open details page
        booksTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    showViewDetailsPage();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(booksTable);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return scroll;
    }

    private void refreshBooksTable() {
        booksModel.setRowCount(0);
        if (books == null) books = new ArrayList<>();
        for (Book b : books) {
            booksModel.addRow(new Object[]{
                    b.getId(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory(),
                    String.format("%.2f", b.getPrice())
            });
        }
    }

    // ============================================================
    // ===================== VIEW BOOK DETAILS (MERGED UI) =========
    // ============================================================
    private void showViewDetailsPage() {
        contentPanel.removeAll();

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Main card
        JPanel mainCard = new JPanel(new BorderLayout());
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 236, 245)),
                new EmptyBorder(18, 18, 18, 18)
        ));

        // Title area (same style as second code)
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("View Book Details");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(20, 26, 36));

        JLabel subtitle = new JLabel("Quick overview of your inventory");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(110, 120, 135));

        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitle);

        top.add(titleBlock, BorderLayout.WEST);

        // Cards grid
        int totalBooks = (books == null) ? 0 : books.size();
        int totalCategories = (categories == null) ? 0 : categories.size();

        JPanel cards = new JPanel(new GridLayout(1, 2, 14, 0));
        cards.setOpaque(false);
        cards.setBorder(new EmptyBorder(18, 0, 0, 0));

        JPanel booksCard = buildModernStatCard("Total Books", String.valueOf(totalBooks), PRIMARY_BLUE, "📚");
        JPanel catsCard  = buildModernStatCard("Total Categories", String.valueOf(totalCategories), GREEN_COLOR, "🗂");

        cards.add(booksCard);
        cards.add(catsCard);

        // Bottom actions
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton backBtn = new JButton("Back to Books");
        styleBlueButton(backBtn);
        backBtn.addActionListener(e -> showBooksPage());
        bottom.add(backBtn);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(cards, BorderLayout.NORTH);
        centerWrap.add(bottom, BorderLayout.SOUTH);

        mainCard.add(top, BorderLayout.NORTH);
        mainCard.add(centerWrap, BorderLayout.CENTER);

        page.add(mainCard, BorderLayout.CENTER);
        contentPanel.add(page, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Modern stat card helper (from your second code style)
    private JPanel buildModernStatCard(String labelText, String valueText, Color accent, String iconText) {
        Color BORDER = new Color(230, 235, 240);
        Color TEXT_DARK = new Color(20, 26, 36);
        Color TEXT_MUTED = new Color(110, 120, 135);

        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accent), // left accent bar
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(16, 16, 16, 16)
                )
        ));

        JLabel icon = new JLabel(iconText);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        icon.setForeground(accent);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_MUTED);

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.BOLD, 34));
        value.setForeground(TEXT_DARK);

        text.add(label);
        text.add(Box.createVerticalStrut(6));
        text.add(value);

        card.add(icon, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);

        return card;
    }

    // ===================== COMMON UI (same as AdminDashboard) ======================
    private void styleBlueButton(JButton btn) {
        btn.setBackground(PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(PRIMARY_BLUE_HOVER); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(PRIMARY_BLUE); }
        });
    }

    private void styleGreenButton(JButton btn) {
        Color hover = new Color(33, 136, 56);
        btn.setBackground(GREEN_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(GREEN_COLOR); }
        });
    }

    private void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(210, 230, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_BLUE);
        header.setForeground(Color.WHITE);
    }

    private void centerAlignAllColumns(JTable table) {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
    }

    private void centerAlignHeader(JTable table) {
        DefaultTableCellRenderer headerCenter =
                (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerCenter.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // ===================== DATA STORE (same as AdminDashboard) =====================
    @SuppressWarnings("unchecked")
    private <T> ArrayList<T> loadList(String file) {
        Object obj = DataStore.load(file);
        try {
            return (ArrayList<T>) obj;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ===================== MAIN (optional test) =====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CashierDashboard("cashier").setVisible(true));
    }
}