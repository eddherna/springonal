package org.edderna.springonal.localenv.containers;

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

import com.moandjiezana.toml.Toml;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoSecurityException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.edderna.springonal.localenv.configuration.mongodb.MongoContainerConfigDb;
import org.edderna.springonal.localenv.container.MongoContainer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoContainerTest {

    @ParameterizedTest
    @ValueSource(strings = {"8.0.13", "7.0.24", "6.0.26", "5.0.31", "4.4.29"})
    void testVersion(String version) throws SQLException {
        var toml = """
                version="%s"
                username="test"
                password="test"
                db-name="dbTest"
                init-scripts=["mongo-scripts/001-create.js", "mongo-scripts/002-insert.js"]
                """;

        var config = new MongoContainerConfigDb(new Toml().read(String.format(toml, version)));
        var container = new MongoContainer(config);

        container.start();

        var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:" + container.getMappedPort(27017)))
                .credential(MongoCredential.createCredential("test", "dbTest", "test".toCharArray()))
                .build();

        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase(config.getDbName());

            // Verify collections exist and have expected data
            long userCount = database.getCollection("users").countDocuments();
            assertThat(userCount).isEqualTo(3).as("Should have 3 users");

            long categoryCount = database.getCollection("categories").countDocuments();
            assertThat(categoryCount).isEqualTo(4).as("Should have 4 categories");

            long productCount = database.getCollection("products").countDocuments();
            assertThat(productCount).isEqualTo(6).as("Should have 6 products");

            // Verify collections exist
            assertThat(database.listCollectionNames()).contains("users", "categories", "products");

        } catch (MongoSecurityException e) {
            System.out.println();
        }finally {
            container.stop();
        }
    }
}
