db.createCollection('products', { capped: false });
db.products.insert([
    { "name": "product1"},
    { "name": "product2"},
]);