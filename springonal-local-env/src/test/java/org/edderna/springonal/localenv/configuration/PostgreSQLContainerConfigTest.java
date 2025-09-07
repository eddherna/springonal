package org.edderna.springonal.localenv.configuration;

import com.moandjiezana.toml.Toml;
import org.edderna.springonal.localenv.configuration.postgre.PostgreSQLContainerConfig;
import org.edderna.springonal.localenv.exception.MalformedEnviromentException;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.*;

public class PostgreSQLContainerConfigTest {
    @Test
    void shouldBuildPostgreSQLContainerConfigWithAllFields() {
        InputStream is = getClass().getResourceAsStream("/postgre-tests-resources/complete-postgre.toml");
        Toml toml = new Toml().read(is).getTable("postgreSQL");
        PostgreSQLContainerConfig config = new PostgreSQLContainerConfig(toml);
        assertThat(config.getVersion()).isEqualTo("15.14-bookworm");
        assertThat(config.isReadWrite()).isTrue();
        assertThat(config.getInitScripts()).containsExactly("./init-script.sql", "./other-script.sql");
        assertThat(config.getUsername()).isEqualTo("adminPostgre");
        assertThat(config.getPassword()).isEqualTo("passwordPostgre");
        assertThat(config.getDbName()).isEqualTo("testdbPostgre");
    }

    @Test
    void shouldBuildPostgreSQLContainerConfigWithDefaultsForMissingFields() {
        InputStream is = getClass().getResourceAsStream("/postgre-tests-resources/default-security-postgre.toml");
        Toml toml = new Toml().read(is).getTable("postgreSQL");
        PostgreSQLContainerConfig config = new PostgreSQLContainerConfig(toml);
        assertThat(config.getVersion()).isEqualTo("2");
        assertThat(config.isReadWrite()).isTrue();
        assertThat(config.getInitScripts()).isNull();
        assertThat(config.getUsername()).isNull();
        assertThat(config.getPassword()).isNull();
        assertThat(config.getDbName()).isEqualTo("testdbPostgre");
    }

    @Test
    void shouldBuildPostgreSQLContainerConfigWithNullDbNameIfMissing() {
        InputStream is = getClass().getResourceAsStream("/postgre-tests-resources/no-db-name-postgre.toml");
        Toml toml = new Toml().read(is).getTable("postgreSQL");
        PostgreSQLContainerConfig config = new PostgreSQLContainerConfig(toml);
        assertThat(config.getVersion()).isEqualTo("2");
        assertThat(config.isReadWrite()).isTrue();
        assertThat(config.getInitScripts()).isNull();
        assertThat(config.getUsername()).isEqualTo("adminPostgre");
        assertThat(config.getPassword()).isEqualTo("passwordPostgre");
        assertThat(config.getDbName()).isNull();
    }

    @Test
    void shouldBuildPostgreSQLContainerConfigWithInitScripts() {
        InputStream is = getClass().getResourceAsStream("/postgre-tests-resources/no-init-postgre.toml");
        Toml toml = new Toml().read(is).getTable("postgreSQL");
        PostgreSQLContainerConfig config = new PostgreSQLContainerConfig(toml);
        assertThat(config.getVersion()).isEqualTo("2");
        assertThat(config.isReadWrite()).isTrue();
        assertThat(config.getInitScripts()).containsExactly("./init-script.sql", "./other-script.sql");
        assertThat(config.getUsername()).isEqualTo("adminPostgre");
        assertThat(config.getPassword()).isEqualTo("passwordPostgre");
        assertThat(config.getDbName()).isEqualTo("testdbPostgre");
    }

    @Test
    void shouldFailToBuildPostgreSQLContainerConfigWithoutVersion() {
        InputStream is = getClass().getResourceAsStream("/postgre-tests-resources/no-version-postgre.toml");
        Toml toml = new Toml().read(is).getTable("postgreSQL");
        assertThatThrownBy(() -> new PostgreSQLContainerConfig(toml))
                .isInstanceOf(MalformedEnviromentException.class)
                .hasMessageContaining("Version definition cannot be null.");
    }
}
