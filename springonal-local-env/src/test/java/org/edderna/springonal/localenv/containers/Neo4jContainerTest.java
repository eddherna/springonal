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
import org.edderna.springonal.localenv.container.Neo4jContainer;
import org.edderna.springonal.localenv.configuration.neo4j.Neo4JContainerConfigDb;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Neo4jContainerTest {

    @ParameterizedTest
    @ValueSource(strings = {"2025.08.0", "5.26.12", "4.4.45"})
    void testVersion(String version) {
        var toml = """
                        version="%s"
                        username="test_user"
                        password="test_password"
                        init-scripts=['neo4j-scripts/001-init.cypher', 'neo4j-scripts/002-init.cypher']
                        console=true
                """;

        Neo4JContainerConfigDb config = new Neo4JContainerConfigDb(new Toml().read(String.format(toml, version)));

        Neo4jContainer container = new Neo4jContainer(config);

        container.start();

        try (var driver = GraphDatabase.driver("neo4j://localhost:" + container.getMappedPort(7687), AuthTokens.basic("test_user",
                "test_password"))) {
            driver.verifyConnectivity();
            var result = driver.executableQuery("MATCH (p:Person:Actor)-[:ACTED_IN]->(m:Movie {title: 'Forrest Gump'}) RETURN p.name AS actor, m.title AS movie;")
                    .execute();
            assertThat(result.records()).hasSize(2);
        }

        assertThat(container.getMappedPort(7474)).isNotNull();
        assertThat(container.getMappedPort(7687)).isNotNull();
        container.stop();
    }

    @Test
    void testNoConsoleExposed() {
        var toml = """
                        version="2025.08.0"
                        username="test_user"
                        password="test_password"
                        init-scripts=['neo4j-scripts/001-init.cypher', 'neo4j-scripts/002-init.cypher']
                        console=false
                """;

        Neo4JContainerConfigDb config = new Neo4JContainerConfigDb(new Toml().read(toml));

        Neo4jContainer container = new Neo4jContainer(config);

        container.start();

        assertThatThrownBy(() -> container.getMappedPort(7474))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        assertThat(container.getMappedPort(7687)).isNotNull();
    }
}
