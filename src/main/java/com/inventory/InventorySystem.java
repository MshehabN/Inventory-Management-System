package com.inventory;

import com.inventory.dao.ProductDAO;
import com.inventory.dao.UserDAO;
import com.inventory.model.Product;
import com.inventory.model.User;

import java.util.List;
import java.util.Scanner;

public class InventorySystem {
    private Scanner scanner;
    private User currentUser;
    private ProductDAO productDAO;
    private UserDAO userDAO;

    public InventorySystem() {
        scanner = new Scanner(System.in);
        productDAO = new ProductDAO();
        userDAO = new UserDAO();
    }

    public void start() {
        if (login()) {
            showMenu();
        } else {
            System.out.println("Login failed. Exiting...");
        }
    }

    private boolean login() {
        System.out.println("=== Inventory Management System ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = userDAO.authenticate(username, password);
        if (currentUser != null) {
            System.out.println("Login successful! Welcome, " + currentUser.getUsername());
            return true;
        }
        return false;
    }

    private void showMenu() {
        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. View Products");
            System.out.println("2. Add Product");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Exit");
            System.out.print("Choice: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    viewProducts();
                    break;
                case 2:
                    if (checkPermission()) addProduct();
                    break;
                case 3:
                    if (checkPermission()) updateProduct();
                    break;
                case 4:
                    if (checkPermission()) deleteProduct();
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private boolean checkPermission() {
        if (!currentUser.isAdmin()) {
            System.out.println("Access denied. Admin role required.");
            return false;
        }
        return true;
    }

    private void viewProducts() {
        List<Product> products = productDAO.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        System.out.println("\n=== Products ===");
        for (Product p : products) {
            System.out.println(p);
        }
    }

    private void addProduct() {
        System.out.print("Product name: ");
        String name = scanner.nextLine();
        System.out.print("Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());
        System.out.print("Price: ");
        double price = Double.parseDouble(scanner.nextLine());

        Product product = new Product(name, quantity, price);
        if (productDAO.addProduct(product)) {
            System.out.println("Product added successfully.");
        } else {
            System.out.println("Failed to add product.");
        }
    }

    private void updateProduct() {
        System.out.print("Product ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        Product product = productDAO.getProductById(id);
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.println("Current: " + product);
        System.out.print("New name (or press Enter to keep): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) product.setName(name);

        System.out.print("New quantity (or press Enter to keep): ");
        String qtyStr = scanner.nextLine();
        if (!qtyStr.isEmpty()) product.setQuantity(Integer.parseInt(qtyStr));

        System.out.print("New price (or press Enter to keep): ");
        String priceStr = scanner.nextLine();
        if (!priceStr.isEmpty()) product.setPrice(Double.parseDouble(priceStr));

        if (productDAO.updateProduct(product)) {
            System.out.println("Product updated successfully.");
        } else {
            System.out.println("Failed to update product.");
        }
    }

    private void deleteProduct() {
        System.out.print("Product ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        if (productDAO.deleteProduct(id)) {
            System.out.println("Product deleted successfully.");
        } else {
            System.out.println("Failed to delete product.");
        }
    }

    public static void main(String[] args) {
        new InventorySystem().start();
    }
}

