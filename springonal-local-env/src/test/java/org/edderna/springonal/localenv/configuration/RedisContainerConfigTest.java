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
import org.edderna.springonal.localenv.configuration.redis.RedisContainerConfigDb;
import org.edderna.springonal.localenv.exception.MalformedEnviromentException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RedisContainerConfigTest {

    @Test
    void shouldReturnCompleteConfigWithCompleteConfigFile() {
        Toml toml = new Toml().read("""
                        version="8.2.1"
                        username="redis_user"
                        password="redis_pass"
                """);

        RedisContainerConfigDb config = new RedisContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "8.2.1")
                .hasFieldOrPropertyWithValue("username", "redis_user")
                .hasFieldOrPropertyWithValue("password", "redis_pass");
    }

    @Test
    void shouldReturnLocalEnvironmentWithOnlyUsernameConfigured() {
        Toml toml = new Toml().read("""
                        version="8.2.1"
                        username="redis_user"
                """);

        RedisContainerConfigDb config = new RedisContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "8.2.1")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test");
    }

    @Test
    void shouldReturnLocalEnvironmentWithOnlyPasswordConfigured() {
        Toml toml = new Toml().read("""
                        version="8.2.1"
                        password="redis_pass"
                """);

        RedisContainerConfigDb config = new RedisContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "8.2.1")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test");
    }

    @Test
    void shouldReturnDefaultValuesWhenEmptyConfiguration() {
        Toml toml = new Toml().read("""
                        version="8.2.1"
                """);

        RedisContainerConfigDb config = new RedisContainerConfigDb(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "8.2.1")
                .hasFieldOrPropertyWithValue("username", "test")
                .hasFieldOrPropertyWithValue("password", "test");
    }

    @Test
    void shouldThrowExceptionWhenVersionIsMissing() {
        Toml toml = new Toml().read("""
                        username="redis_user"
                        password="redis_pass"
                """);

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new RedisContainerConfigDb(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Version definition cannot be null.");
    }

    @Test
    void shouldThrowExceptionWhenTomlIsEmpty() {
        Toml toml = new Toml().read("");

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new RedisContainerConfigDb(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Version definition cannot be null.");
    }
}
