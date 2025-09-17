// Switch to the test database
db = db.getSiblingDB('dbTest');

// Create users collection with validation schema
db.createCollection("users", {
});

// Create categories collection
db.createCollection("categories", {

});

// Create products collection
db.createCollection("products", {

});

// Create indexes for better performance
db.users.createIndex({ "username": 1 }, { unique: true });
db.users.createIndex({ "email": 1 }, { unique: true });
db.products.createIndex({ "categoryId": 1 });
db.products.createIndex({ "createdBy": 1 });
db.categories.createIndex({ "name": 1 });