package ui;

import model.*;
import service.DataStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class AdminDashboard extends JFrame {

    // ===== Layout =====
    private JPanel sidebar;
    private boolean sidebarVisible = true;
    private JPanel contentPanel;

    // ===== Data =====
    private ArrayList<Book> books;
    private ArrayList<Category> categories;
    private ArrayList<User> users;

    private static final String BOOKS_FILE = "books.dat";
    private static final String CATEGORIES_FILE = "categories.dat";
    private static final String USERS_FILE = "users.dat";

    // ===== Tables =====
    private JTable booksTable, categoriesTable, usersTable;
    private DefaultTableModel booksModel, categoriesModel, usersModel;
    private TableRowSorter<DefaultTableModel> booksSorter, categoriesSorter, usersSorter;

    // ===== Theme =====
    private static final Color BG = new Color(245, 247, 250);
    private static final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private static final Color PRIMARY_BLUE_HOVER = new Color(0, 102, 204);
    private static final Color GREEN_COLOR = new Color(40, 167, 69); // ✅ Green for Clear + Edit

    public AdminDashboard(String role) {
        setTitle("Admin Dashboard");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        books = loadList(BOOKS_FILE);
        categories = loadList(CATEGORIES_FILE);
        users = loadList(USERS_FILE);

        // Default users if first run (optional safety)
        if (users == null) users = new ArrayList<>();
        if (users.isEmpty()) {
            users.add(new Admin("admin", "admin123"));
            users.add(new Cashier("cashier", "cash123"));
            saveList(USERS_FILE, users);
        }

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

    // ✅ CORRECT CLICK EVENT
    hamburger.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            toggleSidebar();  // 👈 Calls method below
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

        sidebar.add(sideBtn("Books Management", this::showBooksPage));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(sideBtn("Categories", this::showCategoriesPage));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(sideBtn("Users", this::showUsersPage));

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
    // ===================== BOOKS PAGE ===========================
    // ============================================================
    private void showBooksPage() {
        contentPanel.removeAll();

        JPanel top = buildBooksSearchPanel();
        JScrollPane center = buildBooksTablePanel();
        JPanel bottom = buildActionPanel(this::onAddBook, this::onEditBook, this::onDeleteBook);

        contentPanel.add(top, BorderLayout.NORTH);
        contentPanel.add(center, BorderLayout.CENTER);
        contentPanel.add(bottom, BorderLayout.SOUTH);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ✅ Books: text search + category dropdown + Search + Clear(GREEN)
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
        styleGreenButton(clearBtn); // ✅ Green clear

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

    private void onAddBook() {
        BookForm form = new BookForm(this, "Add Book", null);
        form.setVisible(true);

        Book newBook = form.getResult();
        if (newBook != null) {
            for (Book b : books) {
                if (b.getId().equalsIgnoreCase(newBook.getId())) {
                    JOptionPane.showMessageDialog(this, "Book ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            books.add(newBook);
            saveList(BOOKS_FILE, books);
            refreshBooksTable();
        }
    }

    private void onEditBook() {
        int viewRow = booksTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to edit!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = booksTable.convertRowIndexToModel(viewRow);
        String id = booksModel.getValueAt(modelRow, 0).toString();

        Book found = findBookById(id);
        if (found == null) return;

        BookForm form = new BookForm(this, "Edit Book", found);
        form.setVisible(true);

        Book updated = form.getResult();
        if (updated != null) {
            books.remove(found);
            books.add(updated);
            saveList(BOOKS_FILE, books);
            refreshBooksTable();
        }
    }

    private void onDeleteBook() {
        int viewRow = booksTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = booksTable.convertRowIndexToModel(viewRow);
        String id = booksModel.getValueAt(modelRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Delete book: " + id + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Book found = findBookById(id);
        if (found != null) {
            books.remove(found);
            saveList(BOOKS_FILE, books);
            refreshBooksTable();
        }
    }

    private Book findBookById(String id) {
        for (Book b : books) if (b.getId().equalsIgnoreCase(id)) return b;
        return null;
    }

    // ============================================================
    // ===================== CATEGORIES PAGE ======================
    // ============================================================
    private void showCategoriesPage() {
        contentPanel.removeAll();

        JPanel top = buildCategorySearchPanel();
        JScrollPane center = buildCategoriesTablePanel();
        JPanel bottom = buildActionPanel(this::onAddCategory, this::onEditCategory, this::onDeleteCategory);

        contentPanel.add(top, BorderLayout.NORTH);
        contentPanel.add(center, BorderLayout.CENTER);
        contentPanel.add(bottom, BorderLayout.SOUTH);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel buildCategorySearchPanel() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel sLabel = new JLabel("Search:");
        sLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(280, 34));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        styleBlueButton(searchBtn);
        styleGreenButton(clearBtn);

        searchBtn.addActionListener(e -> {
            if (categoriesSorter == null) return;
            String q = searchField.getText().trim();
            categoriesSorter.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(q)));
        });

        clearBtn.addActionListener(e -> {
            searchField.setText("");
            if (categoriesSorter != null) categoriesSorter.setRowFilter(null);
        });

        top.add(sLabel);
        top.add(searchField);
        top.add(searchBtn);
        top.add(clearBtn);

        return top;
    }

    private JScrollPane buildCategoriesTablePanel() {
        categoriesModel = new DefaultTableModel(new String[]{"Category Name"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        categoriesTable = new JTable(categoriesModel);
        styleTable(categoriesTable);

        categoriesSorter = new TableRowSorter<>(categoriesModel);
        categoriesTable.setRowSorter(categoriesSorter);

        refreshCategoriesTable();

        centerAlignAllColumns(categoriesTable);
        centerAlignHeader(categoriesTable);

        JScrollPane scroll = new JScrollPane(categoriesTable);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return scroll;
    }

    private void refreshCategoriesTable() {
        categoriesModel.setRowCount(0);
        if (categories == null) categories = new ArrayList<>();
        for (Category c : categories) categoriesModel.addRow(new Object[]{c.getName()});
    }

    private void onAddCategory() {
        CategoryForm form = new CategoryForm(this, "Add Category", null);
        form.setVisible(true);

        Category newCat = form.getResult();
        if (newCat != null) {
            for (Category c : categories) {
                if (c.getName().equalsIgnoreCase(newCat.getName())) {
                    JOptionPane.showMessageDialog(this, "Category already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            categories.add(newCat);
            saveList(CATEGORIES_FILE, categories);
            refreshCategoriesTable();
        }
    }

    private void onEditCategory() {
        int viewRow = categoriesTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a category to edit!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = categoriesTable.convertRowIndexToModel(viewRow);
        String name = categoriesModel.getValueAt(modelRow, 0).toString();

        Category found = findCategoryByName(name);
        if (found == null) return;

        CategoryForm form = new CategoryForm(this, "Edit Category", found);
        form.setVisible(true);

        Category updated = form.getResult();
        if (updated != null) {
            categories.remove(found);
            categories.add(updated);
            saveList(CATEGORIES_FILE, categories);
            refreshCategoriesTable();
        }
    }

    private void onDeleteCategory() {
        int viewRow = categoriesTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a category to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = categoriesTable.convertRowIndexToModel(viewRow);
        String name = categoriesModel.getValueAt(modelRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Delete category: " + name + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Category found = findCategoryByName(name);
        if (found != null) {
            categories.remove(found);
            saveList(CATEGORIES_FILE, categories);
            refreshCategoriesTable();
        }
    }

    private Category findCategoryByName(String name) {
        for (Category c : categories) if (c.getName().equalsIgnoreCase(name)) return c;
        return null;
    }

    // ============================================================
    // ===================== USERS PAGE ===========================
    // ============================================================
    private void showUsersPage() {
        contentPanel.removeAll();

        JPanel top = buildUsersSearchPanel();
        JScrollPane center = buildUsersTablePanel();
        JPanel bottom = buildActionPanel(this::onAddUser, this::onEditUser, this::onDeleteUser);

        contentPanel.add(top, BorderLayout.NORTH);
        contentPanel.add(center, BorderLayout.CENTER);
        contentPanel.add(bottom, BorderLayout.SOUTH);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel buildUsersSearchPanel() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel sLabel = new JLabel("Search:");
        sLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(280, 34));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        styleBlueButton(searchBtn);
        styleGreenButton(clearBtn);

        searchBtn.addActionListener(e -> {
            if (usersSorter == null) return;
            String q = searchField.getText().trim();
            usersSorter.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(q)));
        });

        clearBtn.addActionListener(e -> {
            searchField.setText("");
            if (usersSorter != null) usersSorter.setRowFilter(null);
        });

        top.add(sLabel);
        top.add(searchField);
        top.add(searchBtn);
        top.add(clearBtn);

        return top;
    }

    private JScrollPane buildUsersTablePanel() {
        usersModel = new DefaultTableModel(new String[]{"Username", "Password", "Role"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        usersTable = new JTable(usersModel);
        styleTable(usersTable);

        usersSorter = new TableRowSorter<>(usersModel);
        usersTable.setRowSorter(usersSorter);

        refreshUsersTable();

        centerAlignAllColumns(usersTable);
        centerAlignHeader(usersTable);

        JScrollPane scroll = new JScrollPane(usersTable);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return scroll;
    }

    private void refreshUsersTable() {
        usersModel.setRowCount(0);
        if (users == null) users = new ArrayList<>();
        for (User u : users) {
            usersModel.addRow(new Object[]{u.getUsername(), u.getPassword(), u.getRole()});
        }
    }

    private void onAddUser() {
        UserForm form = new UserForm(this, "Add User", null);
        form.setVisible(true);

        User newUser = form.getResult();
        if (newUser != null) {
            for (User u : users) {
                if (u.getUsername().equalsIgnoreCase(newUser.getUsername())) {
                    JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            users.add(newUser);
            saveList(USERS_FILE, users);
            refreshUsersTable();
        }
    }

    private void onEditUser() {
        int viewRow = usersTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to edit!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = usersTable.convertRowIndexToModel(viewRow);
        String username = usersModel.getValueAt(modelRow, 0).toString();

        User found = findUserByUsername(username);
        if (found == null) return;

        UserForm form = new UserForm(this, "Edit User", found);
        form.setVisible(true);

        User updated = form.getResult();
        if (updated != null) {
            users.remove(found);
            users.add(updated);
            saveList(USERS_FILE, users);
            refreshUsersTable();
        }
    }

    private void onDeleteUser() {
        int viewRow = usersTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = usersTable.convertRowIndexToModel(viewRow);
        String username = usersModel.getValueAt(modelRow, 0).toString();

        if (username.equalsIgnoreCase("admin")) {
            JOptionPane.showMessageDialog(this, "You cannot delete the default admin user!", "Blocked", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete user: " + username + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        User found = findUserByUsername(username);
        if (found != null) {
            users.remove(found);
            saveList(USERS_FILE, users);
            refreshUsersTable();
        }
    }

    private User findUserByUsername(String username) {
        for (User u : users) if (u.getUsername().equalsIgnoreCase(username)) return u;
        return null;
    }

    // ===================== COMMON UI ======================
    private JPanel buildActionPanel(Runnable onAdd, Runnable onEdit, Runnable onDelete) {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        bottom.setBackground(BG);

        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        styleBlueButton(addBtn);
        styleGreenButton(editBtn);
        styleBlueButton(deleteBtn);

        addBtn.addActionListener(e -> onAdd.run());
        editBtn.addActionListener(e -> onEdit.run());
        deleteBtn.addActionListener(e -> onDelete.run());

        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(deleteBtn);

        return bottom;
    }

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

    // ===================== DATA STORE =====================
    @SuppressWarnings("unchecked")
    private <T> ArrayList<T> loadList(String file) {
        Object obj = DataStore.load(file);
        try {
            return (ArrayList<T>) obj;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveList(String file, Object list) {
        DataStore.save(file, list);
    }

    // ===================== FORMS ==========================
    private static class BookForm extends JDialog {
        private JTextField idField, titleField, authorField, categoryField, priceField;
        private Book result = null;

        BookForm(JFrame parent, String title, Book existing) {
            super(parent, title, true);
            setSize(520, 420);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            getContentPane().setBackground(new Color(245, 247, 250));

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 236, 245)),
                    new EmptyBorder(18, 18, 18, 18)
            ));
            add(card, BorderLayout.CENTER);

            JLabel hTitle = new JLabel(title);
            hTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            card.add(hTitle, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            form.setOpaque(false);
            form.setBorder(new EmptyBorder(16, 0, 10, 0));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 8, 10, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            idField = new JTextField();
            titleField = new JTextField();
            authorField = new JTextField();
            categoryField = new JTextField();
            priceField = new JTextField();

            styleField(idField);
            styleField(titleField);
            styleField(authorField);
            styleField(categoryField);
            styleField(priceField);

            addRow(form, gbc, 0, "Book ID", idField);
            addRow(form, gbc, 1, "Title", titleField);
            addRow(form, gbc, 2, "Author", authorField);
            addRow(form, gbc, 3, "Category", categoryField);
            addRow(form, gbc, 4, "Price", priceField);

            card.add(form, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actions.setOpaque(false);

            JButton cancel = new JButton("Cancel");
            JButton save = new JButton(existing == null ? "Add Book" : "Save Changes");

            cancel.setFocusPainted(false);
            save.setFocusPainted(false);

            actions.add(cancel);
            actions.add(save);
            card.add(actions, BorderLayout.SOUTH);

            if (existing != null) {
                idField.setText(existing.getId());
                idField.setEnabled(false);
                titleField.setText(existing.getTitle());
                authorField.setText(existing.getAuthor());
                categoryField.setText(existing.getCategory());
                priceField.setText(String.valueOf(existing.getPrice()));
            }

            cancel.addActionListener(e -> { result = null; dispose(); });

            save.addActionListener(e -> {
                String id = idField.getText().trim();
                String t = titleField.getText().trim();
                String a = authorField.getText().trim();
                String c = categoryField.getText().trim();
                String p = priceField.getText().trim();

                if (id.isEmpty() || t.isEmpty() || a.isEmpty() || c.isEmpty() || p.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required!", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(p);
                    if (price < 0) throw new NumberFormatException();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Price must be a valid number!", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                result = new Book(id, t, a, c, price);
                dispose();
            });
        }

        private void addRow(JPanel form, GridBagConstraints gbc, int row, String labelText, JTextField field) {
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));

            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
            form.add(label, gbc);

            gbc.gridx = 1; gbc.weightx = 0.65;
            form.add(field, gbc);
        }

        private void styleField(JTextField f) {
            f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 228, 240)),
                    new EmptyBorder(8, 10, 8, 10)
            ));
        }

        public Book getResult() { return result; }
    }

    private static class CategoryForm extends JDialog {
        private JTextField nameField;
        private Category result = null;

        CategoryForm(JFrame parent, String title, Category existing) {
            super(parent, title, true);
            setSize(460, 260);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            getContentPane().setBackground(new Color(245, 247, 250));

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 236, 245)),
                    new EmptyBorder(18, 18, 18, 18)
            ));
            add(card, BorderLayout.CENTER);

            JLabel h = new JLabel(title);
            h.setFont(new Font("Segoe UI", Font.BOLD, 18));
            card.add(h, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridLayout(2, 1, 8, 8));
            form.setOpaque(false);
            form.setBorder(new EmptyBorder(12, 0, 12, 0));

            JLabel l = new JLabel("Category Name");
            l.setFont(new Font("Segoe UI", Font.BOLD, 13));

            nameField = new JTextField();
            nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            nameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 228, 240)),
                    new EmptyBorder(8, 10, 8, 10)
            ));

            form.add(l);
            form.add(nameField);
            card.add(form, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actions.setOpaque(false);

            JButton cancel = new JButton("Cancel");
            JButton save = new JButton(existing == null ? "Add" : "Save");

            cancel.setFocusPainted(false);
            save.setFocusPainted(false);

            cancel.addActionListener(e -> { result = null; dispose(); });

            save.addActionListener(e -> {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Category name is required!", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                result = new Category(name);
                dispose();
            });

            actions.add(cancel);
            actions.add(save);
            card.add(actions, BorderLayout.SOUTH);

            if (existing != null) nameField.setText(existing.getName());
        }

        public Category getResult() { return result; }
    }

    private static class UserForm extends JDialog {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JComboBox<String> roleBox;
        private User result = null;

        UserForm(JFrame parent, String title, User existing) {
            super(parent, title, true);
            setSize(520, 320);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            getContentPane().setBackground(new Color(245, 247, 250));

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 236, 245)),
                    new EmptyBorder(18, 18, 18, 18)
            ));
            add(card, BorderLayout.CENTER);

            JLabel h = new JLabel(title);
            h.setFont(new Font("Segoe UI", Font.BOLD, 18));
            card.add(h, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            form.setOpaque(false);
            form.setBorder(new EmptyBorder(12, 0, 12, 0));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            usernameField = new JTextField();
            passwordField = new JPasswordField();
            roleBox = new JComboBox<>(new String[]{"admin", "cashier"});

            styleField(usernameField);
            styleField(passwordField);
            roleBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            addRow(form, gbc, 0, "Username", usernameField);
            addRow(form, gbc, 1, "Password", passwordField);

            JLabel roleLabel = new JLabel("Role");
            roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
            form.add(roleLabel, gbc);
            gbc.gridx = 1; gbc.weightx = 0.65;
            form.add(roleBox, gbc);

            card.add(form, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actions.setOpaque(false);

            JButton cancel = new JButton("Cancel");
            JButton save = new JButton(existing == null ? "Add User" : "Save Changes");

            cancel.setFocusPainted(false);
            save.setFocusPainted(false);

            cancel.addActionListener(e -> { result = null; dispose(); });

            save.addActionListener(e -> {
                String u = usernameField.getText().trim();
                String p = new String(passwordField.getPassword()).trim();
                String r = roleBox.getSelectedItem().toString();

                if (u.isEmpty() || p.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Username and Password are required!", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                result = r.equalsIgnoreCase("admin") ? new Admin(u, p) : new Cashier(u, p);
                dispose();
            });

            actions.add(cancel);
            actions.add(save);
            card.add(actions, BorderLayout.SOUTH);

            if (existing != null) {
                usernameField.setText(existing.getUsername());
                usernameField.setEnabled(false);
                passwordField.setText(existing.getPassword());
                roleBox.setSelectedItem(existing.getRole());
            }
        }

        private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
            JLabel l = new JLabel(label);
            l.setFont(new Font("Segoe UI", Font.BOLD, 13));
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
            form.add(l, gbc);
            gbc.gridx = 1; gbc.weightx = 0.65;
            form.add(field, gbc);
        }

        private void styleField(JTextField f) {
            f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 228, 240)),
                    new EmptyBorder(8, 10, 8, 10)
            ));
        }

        public User getResult() { return result; }
    }
}
