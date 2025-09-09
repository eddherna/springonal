CREATE TABLE IF NOT EXISTS other_test_table (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    test_table_id INTEGER NOT NULL REFERENCES test_table(id)
);

INSERT INTO other_test_table (name, test_table_id) VALUES ('test1', 2), ('test2', 1);
