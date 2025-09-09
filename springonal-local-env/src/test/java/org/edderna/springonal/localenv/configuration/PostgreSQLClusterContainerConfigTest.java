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
import org.edderna.springonal.localenv.configuration.postgre.PostgreSQLContainerConfig;
import org.edderna.springonal.localenv.exception.MalformedEnviromentException;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.*;

public class PostgreSQLClusterContainerConfigTest {
    @Test
    void shouldBuildPostgreSQLContainerConfigWithAllFields() {
        InputStream is = getClass().getResourceAsStream("/postgre-tests-resources/complete-postgre.toml");
        Toml toml = new Toml().read(is).getTable("postgreSQL");
        PostgreSQLContainerConfig config = new PostgreSQLContainerConfig(toml);
        assertThat(config.getVersion()).isEqualTo("15.14-bookworm");
        assertThat(config.getInitScripts()).containsExactly("/postgre-tests-resources/init-scripts/init-script.sql", "/postgre-tests-resources/init-scripts/other-script.sql");
        assertThat(config.getUsername()).isEqualTo("adminPostgre");
        assertThat(config.getPassword()).isEqualTo("passwordPostgre");
        assertThat(config.getDbName()).isEqualTo("testdbPostgre");
    }

    @Test
    void shouldBuildPostgreSQLContainerConfigWithDefaultsForMissingFields() {
        InputStream is = getClass().getResourceAsStream("/postgre-tests-resources/default-security-postgre.toml");
        Toml toml = new Toml().read(is).getTable("postgreSQL");
        PostgreSQLContainerConfig config = new PostgreSQLContainerConfig(toml);
        assertThat(config.getVersion()).isEqualTo("15.14-bookworm");
        assertThat(config.getInitScripts()).isEmpty();
        assertThat(config.getUsername()).isEqualTo("admin");
        assertThat(config.getPassword()).isEqualTo("password");
        assertThat(config.getDbName()).isEqualTo("testdbPostgre");
    }

    @Test
    void shouldBuildPostgreSQLContainerConfigWithNullDbNameIfMissing() {
        InputStream is = getClass().getResourceAsStream("/postgre-tests-resources/no-db-name-postgre.toml");
        Toml toml = new Toml().read(is).getTable("postgreSQL");
        PostgreSQLContainerConfig config = new PostgreSQLContainerConfig(toml);
        assertThat(config.getVersion()).isEqualTo("15.14-bookworm");
        assertThat(config.getInitScripts()).isEmpty();
        assertThat(config.getUsername()).isEqualTo("adminPostgre");
        assertThat(config.getPassword()).isEqualTo("passwordPostgre");
        assertThat(config.getDbName()).isEqualTo("test");
    }

    @Test
    void shouldBuildPostgreSQLContainerConfigWithInitScripts() {
        InputStream is = getClass().getResourceAsStream("/postgre-tests-resources/no-init-postgre.toml");
        Toml toml = new Toml().read(is).getTable("postgreSQL");
        PostgreSQLContainerConfig config = new PostgreSQLContainerConfig(toml);
        assertThat(config.getVersion()).isEqualTo("15.14-bookworm");
        
        assertThat(config.getInitScripts()).containsExactly("./init-script.sql", "./other-script.sql");
        assertThat(config.getUsername()).isEqualTo("adminPostgre");
        assertThat(config.getPassword()).isEqualTo("passwordPostgre");
        assertThat(config.getDbName()).isEqualTo("testdbPostgre");
    }

    //TODO: Test cuando no hay read replicas

    @Test
    void shouldFailToBuildPostgreSQLContainerConfigWithoutVersion() {
        InputStream is = getClass().getResourceAsStream("/postgre-tests-resources/no-version-postgre.toml");
        Toml toml = new Toml().read(is).getTable("postgreSQL");
        assertThatThrownBy(() -> new PostgreSQLContainerConfig(toml))
                .isInstanceOf(MalformedEnviromentException.class)
                .hasMessageContaining("Version definition cannot be null.");
    }
}

