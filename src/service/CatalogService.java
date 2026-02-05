package service;

import domain.Product;
import persistence.ProductRepository;
import util.IdUtil;
import util.Validators;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CatalogService {
    private final ProductRepository products;

    public CatalogService(ProductRepository products) {
        this.products = products;
    }

    public List<Product> listAll() {
        return products.findAll();
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

        if (queryText != null && !queryText.trim().isEmpty()) {
            String q = queryText.trim().toLowerCase(Locale.ROOT);
            list = list.stream()
                    .filter(p -> p.getTitle().toLowerCase(Locale.ROOT).contains(q))
                    .collect(Collectors.toList());
        }

        if (categoryOrNull != null && !categoryOrNull.trim().isEmpty() && !"ALL".equalsIgnoreCase(categoryOrNull)) {
            String cat = categoryOrNull.trim().toLowerCase(Locale.ROOT);
            list = list.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().toLowerCase(Locale.ROOT).equals(cat))
                    .collect(Collectors.toList());
        }

        Comparator<Product> comp = null;
        if (sortMode != null) {
            switch (sortMode) {

                case TITLE_ASC:
                    comp = Comparator.comparing(Product::getTitle, String.CASE_INSENSITIVE_ORDER);
                    break;

                case  TITLE_DESC:
                    comp = Comparator.comparing(Product::getTitle, String.CASE_INSENSITIVE_ORDER).reversed();
                    break;

                case PRICE_ASC:
                    comp = Comparator.comparing(Product::getPrice);
                    break;

                case PRICE_DESC:
                    comp = Comparator.comparing(Product::getPrice).reversed();
                    break;
            }
        }

        if (comp != null) list = list.stream().sorted(comp).collect(Collectors.toList());
        return list;
    }
}