package ui;

import domain.Cart;
import domain.CartItem;
import domain.Product;
import domain.User;
import service.AppContext;
import service.SortMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {

  private final AppContext ctx;
  private final AppFrame app;

  private final DefaultTableModel productsModel;
  private final JTable productsTable;
  private final DefaultTableModel cartModel;
  private final JTable cartTable;
  private final JLabel totalLabel;


  private final JTextField searchField = new JTextField(16);
  private final JComboBox<String> categoryBox = new JComboBox<>();
  private final JComboBox<String> sortBox = new JComboBox<>(new String[]{
      "TITLE_ASC", "TITLE_DESC", "PRICE_ASC", "PRICE_DESC"
  });

  public CustomerPanel(AppContext ctx, AppFrame app) {
    this.ctx = ctx;
    this.app = app;

    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(10,10,10,10));

    // product:
    productsModel = new DefaultTableModel(
        new Object[]{"ID", "Title", "Category", "Price", "Stock"}, 0
    ) {
      @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    productsTable = new JTable(productsModel);
    productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JPanel productsPanel = new JPanel(new BorderLayout(8,8));
    productsPanel.setBorder(BorderFactory.createTitledBorder("Products"));

    productsPanel.add(buildProductsControls(), BorderLayout.NORTH);
    productsPanel.add(new JScrollPane(productsTable), BorderLayout.CENTER);
    productsPanel.add(buildProductsButtons(), BorderLayout.SOUTH);

    // cart:
    cartModel = new DefaultTableModel(
        new Object[]{"ProductId", "Qty", "UnitPrice", "LineTotal"}, 0
    ) {
      @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    cartTable = new JTable(cartModel);
    cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JPanel cartPanel = new JPanel(new BorderLayout(8,8));
    cartPanel.setBorder(BorderFactory.createTitledBorder("Cart"));
    cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
    cartPanel.add(buildCartButtons(), BorderLayout.SOUTH);

    // split:
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, productsPanel, cartPanel);
    split.setResizeWeight(0.6);
    add(split, BorderLayout.CENTER);

    // footer:
    JPanel footer = new JPanel(new BorderLayout());
    JButton logout = new JButton("Logout");
    logout.addActionListener(e -> {
      ctx.session.logout();
      app.showLogin();
    });

    totalLabel = new JLabel("Total: 0.0");
    totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 16f));

    footer.add(logout, BorderLayout.WEST);
    footer.add(totalLabel, BorderLayout.EAST);
    add(footer, BorderLayout.SOUTH);

    // init
    refreshCategoryBox();
    refreshProductsTable();
    refreshCartTableAndTotal();
  }

  private JPanel buildProductsControls() {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

    JButton apply = new JButton("Apply");
    JButton clear = new JButton("Clear");

    p.add(new JLabel("Search:"));
    p.add(searchField);

    p.add(new JLabel("Category:"));
    p.add(categoryBox);

    p.add(new JLabel("Sort:"));
    p.add(sortBox);

    p.add(apply);
    p.add(clear);

    apply.addActionListener(e -> refreshProductsTable());
    clear.addActionListener(e -> {
      searchField.setText("");
      if (categoryBox.getItemCount() > 0) categoryBox.setSelectedIndex(0);
      sortBox.setSelectedIndex(0);
      refreshProductsTable();
    });

    return p;
  }

  private JPanel buildProductsButtons() {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton addToCart = new JButton("Add to Cart");
    p.add(addToCart);

    addToCart.addActionListener(e -> {
      try {
        User u = ctx.session.requireUser();
        int row = productsTable.getSelectedRow();
        if (row < 0) throw new IllegalArgumentException("Select a product first");

        String productId = (String) productsModel.getValueAt(row, 0);

        String qtyStr = JOptionPane.showInputDialog(this, "Quantity:", "1");
        if (qtyStr == null) return; // cancelled
        int qty = Integer.parseInt(qtyStr.trim());

        ctx.cart.addToCart(u.getId(), productId, qty);
        refreshCartTableAndTotal();
      } catch (Exception ex) {
        UtilDialogs.error(this, ex.getMessage());
      }
    });

    return p;
  }

  private JPanel buildCartButtons() {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton updateQty = new JButton("Update Qty");
    JButton remove = new JButton("Remove");
    JButton checkout = new JButton("Checkout");

    p.add(updateQty);
    p.add(remove);
    p.add(checkout);

    updateQty.addActionListener(e -> {
      try {
        User u = ctx.session.requireUser();
        int row = cartTable.getSelectedRow();
        if (row < 0) throw new IllegalArgumentException("Select an item first");

        String productId = (String) cartModel.getValueAt(row, 0);

        String qtyStr = JOptionPane.showInputDialog(this, "New quantity (0 removes):", "1");
        if (qtyStr == null) return;
        int qty = Integer.parseInt(qtyStr.trim());

        ctx.cart.updateQty(u.getId(), productId, qty);
        refreshCartTableAndTotal();
      } catch (Exception ex) {
        UtilDialogs.error(this, ex.getMessage());
      }
    });

    remove.addActionListener(e -> {
      try {
        User u = ctx.session.requireUser();
        int row = cartTable.getSelectedRow();
        if (row < 0) throw new IllegalArgumentException("Select an item first");

        String productId = (String) cartModel.getValueAt(row, 0);
        ctx.cart.removeFromCart(u.getId(), productId);
        refreshCartTableAndTotal();
      } catch (Exception ex) {
        UtilDialogs.error(this, ex.getMessage());
      }
    });

    checkout.addActionListener(e -> {
      try {
        User u = ctx.session.requireUser();
        ctx.checkout.checkout(u.getId());
        UtilDialogs.info(this, "Purchase completed!");
        refreshProductsTable();
        refreshCartTableAndTotal();
      } catch (Exception ex) {
        UtilDialogs.error(this, ex.getMessage());
      }
    });

    return p;
  }

  private void refreshCategoryBox() {
    categoryBox.removeAllItems();
    categoryBox.addItem("ALL");


    List<Product> list = ctx.catalog.listForCustomer();
    for (Product p : list) {
      String cat = p.getCategory() == null ? "" : p.getCategory().trim();
      boolean exists = false;
      for (int i = 0; i < categoryBox.getItemCount(); i++) {
        if (categoryBox.getItemAt(i).equalsIgnoreCase(cat)) { exists = true; break; }
      }
      if (!cat.isEmpty() && !exists) categoryBox.addItem(cat);
    }
  }

  private void refreshProductsTable() {
    productsModel.setRowCount(0);

    String q = searchField.getText();
    String cat = (String) categoryBox.getSelectedItem();
    String sort = (String) sortBox.getSelectedItem();

    SortMode sortMode = SortMode.valueOf(sort);

    List<Product> list = ctx.catalog.searchSortFilter(
        q,
        cat,
        sortMode,
        true // onlyAvailableForClient
    );

    for (Product p : list) {
      productsModel.addRow(new Object[]{
          p.getId(),
          p.getTitle(),
          p.getCategory(),
          p.getPrice(),
          p.getStock()
      });
    }
  }

  private void refreshCartTableAndTotal() {
    cartModel.setRowCount(0);
    User u = ctx.session.getCurrent();
    if (u == null) {
      totalLabel.setText("Total: 0.0");
      return;
    }

    Cart cart = ctx.cart.getOrCreateCart(u.getId());
    for (CartItem item : cart.getItems()) {
      double line = item.getUnitPrice() * item.getQty();
      cartModel.addRow(new Object[]{
          item.getProductId(),
          item.getQty(),
          item.getUnitPrice(),
          line
      });
    }

    totalLabel.setText("Total: " + ctx.cart.total(u.getId()));
  }
}
