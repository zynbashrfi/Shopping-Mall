package ui;

import domain.Product;
import domain.User;
import service.AppContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {

  private final AppContext ctx;
  private final AppFrame app;

  private final DefaultTableModel model;
  private final JTable table;

  public AdminPanel(AppContext ctx, AppFrame app) {
    this.ctx = ctx;
    this.app = app;

    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(10, 10, 10, 10));

    model = new DefaultTableModel(new Object[]{
        "ID", "Title", "Category", "Price", "Stock", "Available"
    }, 0) {
      @Override public boolean isCellEditable(int row, int col) { return false; }
    };

    table = new JTable(model);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    add(buildTopBar(), BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(buildBottomBar(), BorderLayout.SOUTH);

    refreshTable();
  }

  private JPanel buildTopBar() {
    JPanel p = new JPanel(new BorderLayout());

    JLabel title = new JLabel("Admin Panel - Products");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
    p.add(title, BorderLayout.WEST);

    JButton refresh = new JButton("Refresh");
    refresh.addActionListener(e -> refreshTable());
    p.add(refresh, BorderLayout.EAST);

    return p;
  }

  private JPanel buildBottomBar() {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton addBtn = new JButton("Add");
    JButton editBtn = new JButton("Edit");
    JButton delBtn = new JButton("Delete");
    JButton toggleBtn = new JButton("Toggle Available");
    JButton logoutBtn = new JButton("Logout");

    p.add(addBtn);
    p.add(editBtn);
    p.add(delBtn);
    p.add(toggleBtn);
    p.add(logoutBtn);

    addBtn.addActionListener(e -> onAdd());
    editBtn.addActionListener(e -> onEdit());
    delBtn.addActionListener(e -> onDelete());
    toggleBtn.addActionListener(e -> onToggleAvailable());

    logoutBtn.addActionListener(e -> {
      ctx.session.logout();
      app.showLogin();
    });

    return p;
  }

  private void refreshTable() {
    model.setRowCount(0);
    List<Product> all = ctx.catalog.listAll(); // admin باید همه رو ببینه

    for (Product p : all) {
      model.addRow(new Object[]{
          p.getId(),
          p.getTitle(),
          p.getCategory(),
          p.getPrice(),
          p.getStock(),
          p.isAvailableForClient()
      });
    }

    revalidate();
    repaint();
  }

  private void onAdd() {
    try {
      ProductForm form = ProductForm.create(this, null);
      if (!form.ok) return;

      ctx.catalog.addProduct(
          form.title,
          form.price,
          form.category,
          form.stock,
          form.available
      );

      refreshTable();
    } catch (Exception ex) {
      UtilDialogs.error(this, ex.getMessage());
    }
  }

  private void onEdit() {
    try {
      int row = table.getSelectedRow();
      if (row < 0) throw new IllegalArgumentException("Select a product first");

      String id = (String) model.getValueAt(row, 0);

      Product existing = ctx.catalog.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Product not found"));

      ProductForm form = ProductForm.create(this, existing);
      if (!form.ok) return;

      ctx.catalog.updateProduct(
          id,
          form.title,
          form.price,
          form.category,
          form.stock,
          form.available
      );

      refreshTable();
    } catch (Exception ex) {
      UtilDialogs.error(this, ex.getMessage());
    }
  }

  private void onDelete() {
    try {
      int row = table.getSelectedRow();
      if (row < 0) throw new IllegalArgumentException("Select a product first");

      String id = (String) model.getValueAt(row, 0);

      int confirm = JOptionPane.showConfirmDialog(
          this,
          "Delete product " + id + "?",
          "Confirm",
          JOptionPane.YES_NO_OPTION
      );

      if (confirm != JOptionPane.YES_OPTION) return;

      ctx.catalog.deleteProduct(id);
      refreshTable();
    } catch (Exception ex) {
      UtilDialogs.error(this, ex.getMessage());
    }
  }

  private void onToggleAvailable() {
    try {
      int row = table.getSelectedRow();
      if (row < 0) throw new IllegalArgumentException("Select a product first");

      String id = (String) model.getValueAt(row, 0);

      Product existing = ctx.catalog.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Product not found"));

      ctx.catalog.updateProduct(
          id,
          existing.getTitle(),
          existing.getPrice(),
          existing.getCategory(),
          existing.getStock(),
          !existing.isAvailableForClient()
      );

      refreshTable();
    } catch (Exception ex) {
      UtilDialogs.error(this, ex.getMessage());
    }
  }

  private static class ProductForm {
    boolean ok;

    String title;
    String category;
    double price;
    int stock;
    boolean available;

    static ProductForm create(Component parent, Product existing) {
      ProductForm f = new ProductForm();

      JTextField titleField = new JTextField(existing == null ? "" : safe(existing.getTitle()), 18);
      JTextField categoryField = new JTextField(existing == null ? "" : safe(existing.getCategory()), 18);
      JTextField priceField = new JTextField(existing == null ? "" : String.valueOf(existing.getPrice()), 18);
      JTextField stockField = new JTextField(existing == null ? "" : String.valueOf(existing.getStock()), 18);
      JCheckBox availableBox = new JCheckBox("Available for customer", existing != null && existing.isAvailableForClient());

      JPanel panel = new JPanel(new GridBagLayout());
      GridBagConstraints gc = new GridBagConstraints();
      gc.insets = new Insets(6,6,6,6);
      gc.anchor = GridBagConstraints.WEST;

      gc.gridx = 0; gc.gridy = 0; panel.add(new JLabel("Title:"), gc);
      gc.gridx = 1; panel.add(titleField, gc);

      gc.gridx = 0; gc.gridy = 1; panel.add(new JLabel("Category:"), gc);
      gc.gridx = 1; panel.add(categoryField, gc);

      gc.gridx = 0; gc.gridy = 2; panel.add(new JLabel("Price:"), gc);
      gc.gridx = 1; panel.add(priceField, gc);

      gc.gridx = 0; gc.gridy = 3; panel.add(new JLabel("Stock:"), gc);
      gc.gridx = 1; panel.add(stockField, gc);

      gc.gridx = 1; gc.gridy = 4; panel.add(availableBox, gc);

      int result = JOptionPane.showConfirmDialog(
          parent,
          panel,
          existing == null ? "Add Product" : "Edit Product",
          JOptionPane.OK_CANCEL_OPTION,
          JOptionPane.PLAIN_MESSAGE
      );

      if (result != JOptionPane.OK_OPTION) {
        f.ok = false;
        return f;
      }

      f.title = titleField.getText().trim();
      f.category = categoryField.getText().trim();
      f.available = availableBox.isSelected();

      try {
        f.price = Double.parseDouble(priceField.getText().trim());
      } catch (Exception ex) {
        throw new IllegalArgumentException("Price must be a number");
      }

      try {
        f.stock = Integer.parseInt(stockField.getText().trim());
      } catch (Exception ex) {
        throw new IllegalArgumentException("Stock must be an integer");
      }

      if (f.title.isEmpty()) throw new IllegalArgumentException("Title is required");
      if (f.price < 0) throw new IllegalArgumentException("Price cannot be negative");
      if (f.stock < 0) throw new IllegalArgumentException("Stock cannot be negative");

      f.ok = true;
      return f;
    }

    private static String safe(String s) { return s == null ? "" : s; }
  }
}
