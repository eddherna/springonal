package org.edderna.springonal.localenv.configuration;

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
