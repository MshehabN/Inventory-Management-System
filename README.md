# Inventory Management System

Inventory management system using JDBC and MySQL. Manages products with role-based access control.

## Features

- View products
- Add/Update/Delete products (admin only)
- Regular users can only view

## Setup

1. Make sure MySQL is running
2. Create the database by running: `mysql -u root -p < database.sql`
3. Update the database connection info in `DBConnection.java` (username/password)

## How to Run

First compile the Java files, then run either version:

GUI version:
```
java -cp "target\classes;lib\mysql-connector.jar" com.inventory.InventoryGUI
```

Console version:
```
java -cp "target\classes;lib\mysql-connector.jar" com.inventory.InventorySystem
```

## Login Credentials

- Admin: admin / admin123 (can add/edit/delete)
- User: user / user123 (view only)

