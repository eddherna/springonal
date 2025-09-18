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
import org.edderna.springonal.localenv.configuration.postgre.PostgreSQLContainerConfigDb;
import org.edderna.springonal.localenv.exception.MalformedEnviromentException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PostgreSQLContainerConfigTest {

    @Test
    void shouldCreatePostgreSQLContainerConfigWithCompleteToml() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        db-name="test_database"
                        username="test_username"
                        password="test_password"
                        init-scripts=["script1.sql", "script2.sql"]
                """);
        PostgreSQLContainerConfigDb config = new PostgreSQLContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "1.2.3")
                .hasFieldOrPropertyWithValue("dbName", "test_database")
                .hasFieldOrPropertyWithValue("username", "test_username")
                .hasFieldOrPropertyWithValue("password", "test_password")
                .hasFieldOrPropertyWithValue("initScripts", List.of("script1.sql", "script2.sql"));
    }

    @Test
    void shouldCreatePostgreSQLContainerConfigWithDefaults() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                """);
        PostgreSQLContainerConfigDb config = new PostgreSQLContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "1.2.3")
                .hasFieldOrPropertyWithValue("dbName", "test")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test")
                .hasFieldOrPropertyWithValue("initScripts", List.of());
    }

    @Test
    void shouldCreatePostgreSQLContainerOnlyUserReturnDefault() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        username="only_user"
                """);

        PostgreSQLContainerConfigDb config = new PostgreSQLContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "1.2.3")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test");
    }

    @Test
    void shouldCreatePostgreSQLContainerOnlyPasswordReturnDefault() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        password="only_password"
                """);

        PostgreSQLContainerConfigDb config = new PostgreSQLContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "1.2.3")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test");
    }

    @Test
    void shouldCreatePostgreSQLContainerOnlyDbNameReturnDefault() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        db-name="only_database"
                """);

        PostgreSQLContainerConfigDb config = new PostgreSQLContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "1.2.3")
                .hasFieldOrPropertyWithValue("dbName", "only_database")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test");
    }

    @Test
    void shouldCreatePostgreSQLContainerOnlyInitScripts() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        init-scripts=["init.sql"]
                """);

        PostgreSQLContainerConfigDb config = new PostgreSQLContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "1.2.3")
                .hasFieldOrPropertyWithValue("initScripts", List.of("init.sql"))
                .hasFieldOrPropertyWithValue("dbName", "test");
    }

    @Test
    void shouldThrowExceptionWhenTomlIsEmpty() {
        Toml toml = new Toml().read("");

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new PostgreSQLContainerConfigDb(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Version definition cannot be null.");
    }

    @Test
    void shouldCreatePostgreSQLContainerWithValidSqlInitScripts() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        init-scripts=["init.sql", "schema.sql", "data.sql"]
                """);

        PostgreSQLContainerConfigDb config = new PostgreSQLContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "1.2.3")
                .hasFieldOrPropertyWithValue("initScripts", List.of("init.sql", "schema.sql", "data.sql"));
    }

    @Test
    void shouldThrowExceptionWhenInitScriptDoesNotHaveSqlExtension() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        init-scripts=["init.sql", "setup.js", "data.sql"]
                """);

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new PostgreSQLContainerConfigDb(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Init script 'setup.js' must have .sql extension");
    }

    @Test
    void shouldThrowExceptionWhenInitScriptHasNoExtension() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        init-scripts=["init.sql", "setup", "data.sql"]
                """);

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new PostgreSQLContainerConfigDb(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Init script 'setup' must have .sql extension");
    }

    @Test
    void shouldThrowExceptionWhenInitScriptHasWrongExtension() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        init-scripts=["init.txt"]
                """);

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new PostgreSQLContainerConfigDb(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Init script 'init.txt' must have .sql extension");
    }
}
