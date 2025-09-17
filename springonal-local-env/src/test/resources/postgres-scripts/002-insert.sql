---
-- #%L
-- springonal
-- %%
-- Copyright (C) 2025 Eduardo Daniel Hernandez
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---
-- Insert test users
INSERT INTO users (username, email, password_hash) VALUES
('john_doe', 'john@example.com', '$2a$10$abcdefghijklmnopqrstuvwxyz'),
('jane_smith', 'jane@example.com', '$2a$10$zyxwvutsrqponmlkjihgfedcba'),
('admin_user', 'admin@example.com', '$2a$10$adminhashedpassword123456');

-- Insert categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and accessories'),
('Books', 'Physical and digital books'),
('Clothing', 'Apparel and fashion items'),
('Home & Garden', 'Home improvement and gardening supplies');

-- Insert products
INSERT INTO products (name, description, price, stock_quantity, category_id, created_by) VALUES
('Smartphone', 'Latest Android smartphone with 128GB storage', 699.99, 50, 1, 1),
('Laptop', 'High-performance laptop for developers', 1299.99, 25, 1, 1),
('Java Programming Book', 'Complete guide to Java programming', 49.99, 100, 2, 2),
('T-Shirt', 'Cotton t-shirt in various colors', 19.99, 200, 3, 2),
('Garden Tools Set', 'Complete set of gardening tools', 89.99, 30, 4, 3),
('Wireless Headphones', 'Noise-canceling wireless headphones', 199.99, 75, 1, 3);
