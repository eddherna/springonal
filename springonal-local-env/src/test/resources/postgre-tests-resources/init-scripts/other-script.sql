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
CREATE TABLE IF NOT EXISTS other_test_table (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    test_table_id INTEGER NOT NULL REFERENCES test_table(id)
);

INSERT INTO other_test_table (name, test_table_id) VALUES ('test1', 2), ('test2', 1);
