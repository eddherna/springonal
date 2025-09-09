package org.edderna.springonal.localenv.configuration;

import com.moandjiezana.toml.Toml;
import org.edderna.springonal.localenv.configuration.postgre.PostgreSQLContainerConfig;
import org.edderna.springonal.localenv.configuration.redis.RedisContainerConfig;
import org.edderna.springonal.localenv.exception.MalformedEnviromentException;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RedisContainerConfigTest {

    @Test
    void shouldReturnRedisConfigWithVersion() {
        InputStream is = getClass().getResourceAsStream("/redis-tests-resources/complete-redis.toml");
        RedisContainerConfig rc = new RedisContainerConfig(new Toml().read(is).getTable("redis"));

        assertThat(rc.getVersion()).isEqualTo("8.2.1");


    }

    @Test
    void shouldFailToBuildRedisContainerConfigWithoutVersion() {
        InputStream is = getClass().getResourceAsStream("/redis-tests-resources/invalid-redis.toml");
        assertThatThrownBy(() -> new PostgreSQLContainerConfig(new Toml().read(is).getTable("redis")))
                .isInstanceOf(MalformedEnviromentException.class)
                .hasMessageContaining("Version definition cannot be null.");
    }
}
