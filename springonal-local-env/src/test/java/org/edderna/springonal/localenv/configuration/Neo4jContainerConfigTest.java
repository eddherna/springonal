package org.edderna.springonal.localenv.configuration;

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
import org.edderna.springonal.localenv.configuration.neo4j.Neo4JContainerConfigDb;
import org.edderna.springonal.localenv.exception.MalformedEnviromentException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Neo4jContainerConfigTest {

    @Test
    void shouldCreateNeo4jContainerConfigWithCompleteToml() {
        Toml toml = new Toml().read("""
                        version="5.0.0"
                        username="neo4j_user"
                        password="neo4j_pass"
                        init-scripts=["init.cypher", "schema.cypher"]
                        console=true
                """);

        Neo4JContainerConfigDb config = new Neo4JContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "5.0.0")
                .hasFieldOrPropertyWithValue("username", "neo4j_user")
                .hasFieldOrPropertyWithValue("password", "neo4j_pass")
                .hasFieldOrPropertyWithValue("console", true)
                .hasFieldOrPropertyWithValue("initScripts", List.of("init.cypher", "schema.cypher"));
    }

    @Test
    void shouldCreateNeo4jContainerConfigWithDefaults() {
        Toml toml = new Toml().read("""
                        version="5.0.0"
                """);

        Neo4JContainerConfigDb config = new Neo4JContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "5.0.0")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test")
                .hasFieldOrPropertyWithValue("console", false)
                .hasFieldOrPropertyWithValue("initScripts", List.of());
    }

    @Test
    void shouldCreateNeo4jContainerOnlyUserReturnDefault() {
        Toml toml = new Toml().read("""
                        version="5.0.0"
                        username="only_user"
                """);

        Neo4JContainerConfigDb config = new Neo4JContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "5.0.0")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test");
    }

    @Test
    void shouldCreateNeo4jContainerOnlyPasswordReturnDefault() {
        Toml toml = new Toml().read("""
                        version="5.0.0"
                        password="only_password"
                """);

        Neo4JContainerConfigDb config = new Neo4JContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "5.0.0")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test");
    }

    @Test
    void shouldThrowExceptionWhenVersionIsMissing() {
        Toml toml = new Toml().read("""
                        username="neo4j_user"
                        password="neo4j_pass"
                        init-scripts=["init.cypher"]
                """);

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new Neo4JContainerConfigDb(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Version definition cannot be null.");
    }

    @Test
    void shouldThrowExceptionWhenTomlIsEmpty() {
        Toml toml = new Toml().read("");

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new Neo4JContainerConfigDb(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Version definition cannot be null.");
    }

    @Test
    void shouldCreateNeo4jContainerWithValidCypherInitScripts() {
        Toml toml = new Toml().read("""
                        version="5.0.0"
                        init-scripts=["nodes.cypher", "relationships.cypher", "constraints.cypher"]
                """);

        Neo4JContainerConfigDb config = new Neo4JContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "5.0.0")
                .hasFieldOrPropertyWithValue("initScripts", List.of("nodes.cypher", "relationships.cypher", "constraints.cypher"));
    }

    @Test
    void shouldThrowExceptionWhenInitScriptDoesNotHaveCypherExtension() {
        Toml toml = new Toml().read("""
                        version="5.0.0"
                        init-scripts=["init.cypher", "setup.sql", "data.cypher"]
                """);

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new Neo4JContainerConfigDb(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Init script 'setup.sql' must have .cypher extension");
    }

    @Test
    void shouldThrowExceptionWhenInitScriptHasNoExtension() {
        Toml toml = new Toml().read("""
                        version="5.0.0"
                        init-scripts=["init.cypher", "setup", "data.cypher"]
                """);

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new Neo4JContainerConfigDb(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Init script 'setup' must have .cypher extension");
    }

    @Test
    void shouldThrowExceptionWhenInitScriptHasWrongExtension() {
        Toml toml = new Toml().read("""
                        version="5.0.0"
                        init-scripts=["init.cql"]
                """);

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new Neo4JContainerConfigDb(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Init script 'init.cql' must have .cypher extension");
    }

    @Test
    void shouldThrowExceptionWhenInitScriptHasJsExtension() {
        Toml toml = new Toml().read("""
                        version="5.0.0"
                        init-scripts=["graph.js"]
                """);

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new Neo4JContainerConfigDb(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Init script 'graph.js' must have .cypher extension");
    }
}
