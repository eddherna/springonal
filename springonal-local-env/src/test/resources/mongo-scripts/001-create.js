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
