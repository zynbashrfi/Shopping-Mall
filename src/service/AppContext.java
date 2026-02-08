package service;

public class AppContext {
    public final AuthService auth;
    public final CatalogService catalog;
    public final CartService cart;
    public final CheckoutService checkout;
    public final Session session;

    public AppContext(AuthService auth, CatalogService catalog, CartService cart, CheckoutService checkout, Session session) {
        this.auth = auth;
        this.catalog = catalog;
        this.checkout = checkout;
        this.session = session;
        this.cart = cart;
    }
}