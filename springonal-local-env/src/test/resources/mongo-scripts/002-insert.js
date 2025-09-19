/*-
 * #%L
 * springonal
 * %%
 * Copyright (C) 2025 Eduardo Daniel Hernandez
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
// Switch to the test database

// Insert test users
var users = db.users.insertMany([
    {
        username: "john_doe",
        email: "john@example.com",
        passwordHash: "$2a$10$abcdefghijklmnopqrstuvwxyz",
        createdAt: new Date(),
        isActive: true
    },
    {
        username: "jane_smith",
        email: "jane@example.com",
        passwordHash: "$2a$10$zyxwvutsrqponmlkjihgfedcba",
        createdAt: new Date(),
        isActive: true
    },
    {
        username: "admin_user",
        email: "admin@example.com",
        passwordHash: "$2a$10$adminhashedpassword123456",
        createdAt: new Date(),
        isActive: true
    }
]);

// Insert categories
var categories = db.categories.insertMany([
    {
        name: "Electronics",
        description: "Electronic devices and accessories",
        createdAt: new Date()
    },
    {
        name: "Books",
        description: "Physical and digital books",
        createdAt: new Date()
    },
    {
        name: "Clothing",
        description: "Apparel and fashion items",
        createdAt: new Date()
    },
    {
        name: "Home & Garden",
        description: "Home improvement and gardening supplies",
        createdAt: new Date()
    }
]);

// Get inserted IDs for relationships
var userIds = users.insertedIds;
var categoryIds = categories.insertedIds;

// Insert products with references
db.products.insertMany([
    {
        name: "Smartphone",
        description: "Latest Android smartphone with 128GB storage",
        price: NumberDecimal("699.99"),
        stockQuantity: 50,
        categoryId: categoryIds[0],
        createdBy: userIds[0],
        createdAt: new Date()
    },
    {
        name: "Laptop",
        description: "High-performance laptop for developers",
        price: NumberDecimal("1299.99"),
        stockQuantity: 25,
        categoryId: categoryIds[0],
        createdBy: userIds[0],
        createdAt: new Date()
    },
    {
        name: "Java Programming Book",
        description: "Complete guide to Java programming",
        price: NumberDecimal("49.99"),
        stockQuantity: 100,
        categoryId: categoryIds[1],
        createdBy: userIds[1],
        createdAt: new Date()
    },
    {
        name: "T-Shirt",
        description: "Cotton t-shirt in various colors",
        price: NumberDecimal("19.99"),
        stockQuantity: 200,
        categoryId: categoryIds[2],
        createdBy: userIds[1],
        createdAt: new Date()
    },
    {
        name: "Garden Tools Set",
        description: "Complete set of gardening tools",
        price: NumberDecimal("89.99"),
        stockQuantity: 30,
        categoryId: categoryIds[3],
        createdBy: userIds[2],
        createdAt: new Date()
    },
    {
        name: "Wireless Headphones",
        description: "Noise-canceling wireless headphones",
        price: NumberDecimal("199.99"),
        stockQuantity: 75,
        categoryId: categoryIds[0],
        createdBy: userIds[2],
        createdAt: new Date()
    }
]);
