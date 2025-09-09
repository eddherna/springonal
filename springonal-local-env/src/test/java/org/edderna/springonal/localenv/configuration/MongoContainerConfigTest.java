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
import org.edderna.springonal.localenv.configuration.mongodb.MongoContainerConfig;
import org.edderna.springonal.localenv.exception.MalformedEnviromentException;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MongoContainerConfigTest {
    @Test
    void shouldBuildMongoContainerConfigWithAllFields() {
        InputStream is = getClass().getResourceAsStream("/mongo-tests-resources/complete-mongo-4.toml");
        Toml toml = new Toml().read(is).getTable("mongo");
        MongoContainerConfig config = new MongoContainerConfig(toml);
        assertThat(config.getVersion()).isEqualTo("4.4");
        assertThat(config.getUsername()).isEqualTo("adminMongo");
        assertThat(config.getPassword()).isEqualTo("passwordMongo");
        assertThat(config.getDbName()).isEqualTo("testDbMongo");
        assertThat(config.getInitScripts()).containsExactly(
                "/mongo-tests-resources/mongo-test-init-scripts/collection-1.js",
                "/mongo-tests-resources/mongo-test-init-scripts/collection-2.js"
        );
    }

    @Test
    void shouldBuildMongoContainerConfigWithDefaultsForMissingFields() {
        InputStream is = getClass().getResourceAsStream("/mongo-tests-resources/default-security-mongo.toml");
        Toml toml = new Toml().read(is).getTable("mongo");
        MongoContainerConfig config = new MongoContainerConfig(toml);
        assertThat(config.getVersion()).isEqualTo("4.4");
        assertThat(config.getUsername()).isEqualTo("admin");
        assertThat(config.getPassword()).isEqualTo("password");
        assertThat(config.getDbName()).isEqualTo("testDbMongo");
        assertThat(config.getInitScripts()).isEmpty();
    }

    @Test
    void shouldBuildMongoContainerConfigWithDefaultDbNameIfMissing() {
        InputStream is = getClass().getResourceAsStream("/mongo-tests-resources/no-db-name-mongo.toml");
        Toml toml = new Toml().read(is).getTable("mongo");
        MongoContainerConfig config = new MongoContainerConfig(toml);
        assertThat(config.getVersion()).isEqualTo("4.4");
        assertThat(config.getUsername()).isEqualTo("adminMongo");
        assertThat(config.getPassword()).isEqualTo("passwordMongo");
        assertThat(config.getDbName()).isEqualTo("test");
        assertThat(config.getInitScripts()).isEmpty();
    }

    @Test
    void shouldBuildMongoContainerConfigWithDefaultInitScriptsIfMissing() {
        InputStream is = getClass().getResourceAsStream("/mongo-tests-resources/no-init-mongo.toml");
        Toml toml = new Toml().read(is).getTable("mongo");
        MongoContainerConfig config = new MongoContainerConfig(toml);
        assertThat(config.getVersion()).isEqualTo("4.4");
        assertThat(config.getUsername()).isEqualTo("adminMongo");
        assertThat(config.getPassword()).isEqualTo("passwordMongo");
        assertThat(config.getDbName()).isEqualTo("testDbMongo");
        assertThat(config.getInitScripts()).isEmpty();
    }

    @Test
    void shouldFailToBuildMongoContainerConfigWithoutVersion() {
        InputStream is = getClass().getResourceAsStream("/mongo-tests-resources/no-version-mongo.toml");
        Toml toml = new Toml().read(is).getTable("mongo");
        assertThatThrownBy(() -> new MongoContainerConfig(toml))
                .isInstanceOf(MalformedEnviromentException.class)
                .hasMessageContaining("Version definition cannot be null.");
    }
}
