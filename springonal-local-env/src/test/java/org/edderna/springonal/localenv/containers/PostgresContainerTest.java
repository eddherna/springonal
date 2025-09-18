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
import org.edderna.springonal.localenv.configuration.postgre.PostgreSQLContainerConfigDb;
import org.edderna.springonal.localenv.container.PostgresContainer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostgresContainerTest {

    @ParameterizedTest
    @ValueSource(strings = {"17.6", "16.10", "15.14"})
    void testVersion(String version) throws SQLException {
        var toml = """
                version="%s"
                username="test"
                password="test"
                db-name="dbTest"
                init-scripts=["postgres-scripts/001-create.sql", "postgres-scripts/002-insert.sql"]
                """;

        var config = new PostgreSQLContainerConfigDb(new Toml().read(String.format(toml, version)));
        var container = new PostgresContainer(config);

        container.start();


        // Get connection details
        String jdbcUrl = String.format("jdbc:postgresql://localhost:%d/dbTest",
                container.getMappedPort(5432));
        String username = config.getUsername();
        String password = config.getPassword();

        // Test database connection and verify data
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement()) {

            // Verify tables exist and have expected data
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            rs.next();
            int userCount = rs.getInt(1);

            assertThat(userCount).isEqualTo(3);


            rs = stmt.executeQuery("SELECT COUNT(*) FROM categories");
            rs.next();
            int categoryCount = rs.getInt(1);
            assertThat(categoryCount).isEqualTo(4);

            rs = stmt.executeQuery("SELECT COUNT(*) FROM products");
            rs.next();
            int productCount = rs.getInt(1);
            assertThat(productCount).isEqualTo(6);

        } finally {
            container.stop();
        }
    }
}
