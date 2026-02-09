package service;

import domain.Product;
import persistence.ProductRepository;
import util.IdUtil;
import util.Validators;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.Optional;

public class CatalogService {
    private final ProductRepository products;

    public CatalogService(ProductRepository products) {
        this.products = products;
    }

    public List<Product> listAll() {
        return products.findAll();
    }

    public Optional<Product> findById(String id) {
        return products.findById(id);
    }

    public List<Product> listForCustomer() {
        return products.findAll().stream()
                .filter(Product::isAvailableForClient)
                .collect(Collectors.toList());
    }

    public Product addProduct(String title, double price, String category, int stock, boolean availableForClient) {
        Validators.requireNonBlank(title, "Title");
        Validators.requireNonBlank(category, "Category");
        if (price <= 0) throw new IllegalArgumentException("Price must be more than zero.");
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative.");

        List<Product> all = products.findAll();
        Product p = new Product(
                IdUtil.newProductsId(),
                title.trim(),
                price,
                category.trim(),
                stock,
                availableForClient
        );
        all.add(p);
        products.saveAll(all);
        return p;
    }

    public void updateProduct(Product updated) {
        List<Product> all = products.findAll();

        Product existing = all.stream()
                .filter(p -> p.getId().equals(updated.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));

        Validators.requireNonBlank(updated.getTitle(), "Title");
        Validators.requireNonBlank(updated.getCategory(), "Category");
        if (updated.getPrice() <= 0) throw new IllegalArgumentException("Price must be more than zero.");
        if (updated.getStock() <  0) throw new IllegalArgumentException("Stock cannot be negative.");

        existing.setTitle(updated.getTitle().trim());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory().trim());
        existing.setAvailableForClient(updated.isAvailableForClient());

        products.saveAll(all);

    }

    public void updateProduct(String id, String title, double price, String category, int stock, boolean availableForClient) {
        Product p = findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        p.setTitle(title);
        p.setPrice(price);
        p.setCategory(category);
        p.setStock(stock);
        p.setAvailableForClient(availableForClient);
        updateProduct(p);
    }

    public void deleteProduct(String productId) {
        List<Product> all = products.findAll();
        boolean removed = all.removeIf(p -> p.getId().equals(productId));
        if (!removed) throw new IllegalArgumentException("Product not found.");
        products.saveAll(all);
    }

    public List<Product> searchSortFilter(
            String queryText,
            String categoryOrNull,
            SortMode sortMode,
            boolean onlyAvailableForClient
    ) {
        List<Product> list = products.findAll();

        if (onlyAvailableForClient)
            list = list.stream().filter(Product::isAvailableForClient).collect(Collectors.toList());

        if (queryText != null) {
            String q = queryText.trim().toLowerCase(Locale.ROOT);
            if (!q.isEmpty()) {
                list = list.stream()
                        .filter(p -> p.getTitle() != null &&
                                p.getTitle().toLowerCase(java.util.Locale.ROOT).contains(q))
                        .collect(java.util.stream.Collectors.toList());
            }
        }

        if (categoryOrNull != null ) {
            String cat = categoryOrNull.trim();
            if (!cat.isEmpty() && !"ALL".equalsIgnoreCase(cat)) {
                String catLower = cat.toLowerCase(java.util.Locale.ROOT);
                list = list.stream()
                        .filter(p -> p.getCategory() != null &&
                                p.getCategory().trim().toLowerCase(java.util.Locale.ROOT).equals(catLower))
                        .collect(java.util.stream.Collectors.toList());
            }
        }

        java.util.Comparator<Product> comp = null;
        if (sortMode != null) {
            switch (sortMode) {

                case TITLE_ASC:
                    comp = java.util.Comparator.comparing(
                            (Product p) -> p.getTitle() == null ? "" : p.getTitle(),
                            String.CASE_INSENSITIVE_ORDER
                    );
                    break;

                case TITLE_DESC:
                    comp = java.util.Comparator.comparing(
                            (Product p) -> p.getTitle() == null ? "" : p.getTitle(),
                            String.CASE_INSENSITIVE_ORDER
                    ).reversed();
                    break;

                case PRICE_ASC:
                    comp = java.util.Comparator.comparingDouble(Product::getPrice);
                    break;

                case PRICE_DESC:
                    comp = java.util.Comparator.comparingDouble(Product::getPrice).reversed();
            }
        }

        if (comp != null) {
            list = list.stream()
                    .sorted(comp)
                    .collect(java.util.stream.Collectors.toList());
        }
        return list;
    }
}