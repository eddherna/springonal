INSERT INTO users (id, username, email, created_at, is_active)
VALUES (uuid(), 'admin', 'admin@example.com', toTimestamp(now()), true);

INSERT INTO users (id, username, email, created_at, is_active)
VALUES (uuid(), 'testuser', 'test@example.com', toTimestamp(now()), true);

INSERT INTO products (id, name, description, price, category, created_at)
VALUES (uuid(), 'Test Product', 'A sample product for testing', 29.99, 'electronics', toTimestamp(now()));