# Shopping Mall (Java + Swing + JSON)

## Requirements
- Java JDK 25
- IntelliJ IDEA (Community)

## How to Run
1. (Optional) Reset demo data:
    - Run `reset_data.bat` (Windows)
2. Open the project in IntelliJ.
3. Add `lib/gson-2.13.2.jar` to Project Libraries (if not already).
4. Run: `app.Main`

## Demo Accounts
- Admin:
    - username: `admin`
    - password: `admin`
- Customer:
    - username: `testuser`
    - password: `1234`

## Features
### Authentication
- Sign up (customer)
- Sign in (admin/customer)
- Role-based routing (Admin panel / Customer panel)

### Products
- Admin: add / edit / delete products
- Admin: toggle product visibility for customers
- Customer: view available products

### Cart & Checkout
- Customer: add/update/remove items in cart
- Cart total calculation
- Checkout:
    - validates stock
    - decreases product stock
    - clears cart
    - persists changes in JSON files

### Bonus
- Customer: Search / Filter by category / Sort (title, price)

## Data Storage (JSON)
- `data/users.json`
- `data/products.json`
- `data/carts.json`

For clean demo data:
- `data_seed/` contains stable demo JSON files.
- `reset_data.bat` copies demo data to `data/`.
