CREATE DATABASE IF NOT EXISTS inventory_db;
USE inventory_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    CHECK (role IN ('ADMIN', 'USER'))
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    price DECIMAL(10,2) NOT NULL
);

INSERT INTO users (username, password, role) VALUES 
('admin', 'admin123', 'ADMIN'),
('user', 'user123', 'USER');

INSERT INTO products (name, quantity, price) VALUES 
('Laptop', 50, 999.99),
('Mouse', 200, 15.50),
('Keyboard', 150, 45.00);

