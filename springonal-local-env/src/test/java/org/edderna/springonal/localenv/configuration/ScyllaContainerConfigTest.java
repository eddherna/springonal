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
import org.edderna.springonal.localenv.configuration.scylla.ScyllaContainerConfig;
import org.edderna.springonal.localenv.exception.MalformedEnviromentException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScyllaContainerConfigTest {

    @Test
    void shouldCreateScyllaContainerConfigWithCompleteToml() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        keyspace="test_keyspace"
                        username="test_username"
                        password="test_password"
                        init-scripts=["script1.cql", "script2.cql"]
                """);
        ScyllaContainerConfig config = new ScyllaContainerConfig(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "1.2.3")
                .hasFieldOrPropertyWithValue("keyspace", "test_keyspace")
                .hasFieldOrPropertyWithValue("username", "test_username")
                .hasFieldOrPropertyWithValue("password", "test_password")
                .hasFieldOrPropertyWithValue("initScripts", List.of("script1.cql", "script2.cql"));

    }

    @Test
    void shouldCreateScyllaContainerConfigWithDefaults() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                """);
        ScyllaContainerConfig config = new ScyllaContainerConfig(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "1.2.3")
                .hasFieldOrPropertyWithValue("keyspace", "test")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test")
                .hasFieldOrPropertyWithValue("initScripts", List.of());
    }

    @Test
    void shouldCreateScyllaContainerOnlyUserReturnDefault() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        username="only_user"
                """);

        ScyllaContainerConfig config = new ScyllaContainerConfig(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "1.2.3")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test");
    }

    @Test
    void shouldCreateScyllaContainerOnlyPasswordReturnDefault() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        password="only_password"
                """);

        ScyllaContainerConfig config = new ScyllaContainerConfig(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "1.2.3")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test");
    }

    @Test
    void shouldThrowExceptionWhenTomlIsEmpty() {
        Toml toml = new Toml().read("");

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new ScyllaContainerConfig(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Version definition cannot be null.");
    }

    @Test
    void shouldCreateScyllaContainerWithValidCqlInitScripts() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        init-scripts=["init.cql", "schema.cql", "data.cql"]
                """);

        ScyllaContainerConfig config = new ScyllaContainerConfig(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "1.2.3")
                .hasFieldOrPropertyWithValue("initScripts", List.of("init.cql", "schema.cql", "data.cql"));
    }

    @Test
    void shouldThrowExceptionWhenInitScriptDoesNotHaveCqlExtension() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        init-scripts=["init.cql", "setup.sql", "data.cql"]
                """);

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new ScyllaContainerConfig(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Init script 'setup.sql' must have .cql extension");
    }

    @Test
    void shouldThrowExceptionWhenInitScriptHasNoExtension() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        init-scripts=["init.cql", "setup", "data.cql"]
                """);

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new ScyllaContainerConfig(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Init script 'setup' must have .cql extension");
    }

    @Test
    void shouldThrowExceptionWhenInitScriptHasWrongExtension() {
        Toml toml = new Toml().read("""
                        version="1.2.3"
                        init-scripts=["init.js"]
                """);

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new ScyllaContainerConfig(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Init script 'init.js' must have .cql extension");
    }
}
