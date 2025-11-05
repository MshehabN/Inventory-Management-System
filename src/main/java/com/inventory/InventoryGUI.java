package com.inventory;

import com.inventory.dao.ProductDAO;
import com.inventory.dao.UserDAO;
import com.inventory.model.Product;
import com.inventory.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryGUI extends JFrame {
    private User currentUser;
    private ProductDAO productDAO;
    private UserDAO userDAO;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, qtyField, priceField, idField;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn;

    public InventoryGUI() {
        productDAO = new ProductDAO();
        userDAO = new UserDAO();
        
        if (!showLogin()) {
            System.exit(0);
        }
        
        initComponents();
        loadProducts();
    }

    private boolean showLogin() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        
        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        
        int result = JOptionPane.showConfirmDialog(null, panel, 
            "Login", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            currentUser = userDAO.authenticate(userField.getText(), 
                new String(passField.getPassword()));
            if (currentUser != null) {
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials");
            }
        }
        return false;
    }

    private void initComponents() {
        setTitle("Inventory Management System - " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Table
        String[] columns = {"ID", "Name", "Quantity", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(productTable);
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        
        idField = new JTextField();
        idField.setEditable(false);
        nameField = new JTextField();
        qtyField = new JTextField();
        priceField = new JTextField();
        
        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(qtyField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addBtn = new JButton("Add");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        refreshBtn = new JButton("Refresh");
        
        addBtn.addActionListener(e -> addProduct());
        updateBtn.addActionListener(e -> updateProduct());
        deleteBtn.addActionListener(e -> deleteProduct());
        refreshBtn.addActionListener(e -> loadProducts());
        
        if (!currentUser.isAdmin()) {
            addBtn.setEnabled(false);
            updateBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        
        // Product table selection
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = productTable.getSelectedRow();
                if (row >= 0) {
                    idField.setText(tableModel.getValueAt(row, 0).toString());
                    nameField.setText(tableModel.getValueAt(row, 1).toString());
                    qtyField.setText(tableModel.getValueAt(row, 2).toString());
                    priceField.setText(tableModel.getValueAt(row, 3).toString());
                }
            }
        });
        
        // Layout
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        
        setSize(700, 500);
        setLocationRelativeTo(null);
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getQuantity(), p.getPrice()
            });
        }
        clearFields();
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        qtyField.setText("");
        priceField.setText("");
    }

    private void addProduct() {
        try {
            String name = nameField.getText().trim();
            int qty = Integer.parseInt(qtyField.getText());
            double price = Double.parseDouble(priceField.getText());
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty");
                return;
            }
            
            Product p = new Product(name, qty, price);
            if (productDAO.addProduct(p)) {
                JOptionPane.showMessageDialog(this, "Product added successfully");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add product");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format");
        }
    }

    private void updateProduct() {
        try {
            if (idField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a product to update");
                return;
            }
            
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText().trim();
            int qty = Integer.parseInt(qtyField.getText());
            double price = Double.parseDouble(priceField.getText());
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty");
                return;
            }
            
            Product p = new Product(name, qty, price);
            p.setId(id);
            
            if (productDAO.updateProduct(p)) {
                JOptionPane.showMessageDialog(this, "Product updated successfully");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update product");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format");
        }
    }

    private void deleteProduct() {
        try {
            if (idField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a product to delete");
                return;
            }
            
            int id = Integer.parseInt(idField.getText());
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (productDAO.deleteProduct(id)) {
                    JOptionPane.showMessageDialog(this, "Product deleted successfully");
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete product");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new InventoryGUI().setVisible(true);
        });
    }
}

