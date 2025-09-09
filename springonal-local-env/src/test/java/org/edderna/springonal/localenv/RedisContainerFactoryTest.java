package org.edderna.springonal.localenv;

import com.moandjiezana.toml.Toml;
import org.edderna.springonal.localenv.configuration.redis.RedisContainerConfig;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import redis.clients.jedis.Jedis;

import java.io.InputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RedisContainerFactoryTest {

    RedisContainerFactory rcf = new RedisContainerFactory();

    @Test
    void shouldReturnStartableRedisContainer() {
        InputStream is = getClass().getResourceAsStream("/redis-tests-resources/complete-redis.toml");
        RedisContainerConfig rc = new RedisContainerConfig(new Toml().read(is).getTable("redis"));

        try (GenericContainer<?> container = rcf.create(rc)) {
            container.start();

            try (Jedis jedis = new Jedis("localhost", container.getMappedPort(6379))) {
                String response = jedis.ping();

                assertThat(response).isEqualTo("PONG");
            }
        }
    }
}
