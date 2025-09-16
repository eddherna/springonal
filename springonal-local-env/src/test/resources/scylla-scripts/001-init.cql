CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username TEXT,
    email TEXT,
    created_at TIMESTAMP,
    is_active BOOLEAN);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    name TEXT,
    description TEXT,
    price DECIMAL,
    category TEXT,
    created_at TIMESTAMP
);