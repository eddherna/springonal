package org.edderna.springonal.localenv;

import org.edderna.springonal.localenv.configuration.redis.RedisContainerConfig;
import org.testcontainers.containers.GenericContainer;

public class RedisContainerFactory extends ContainerFactory<RedisContainerConfig> {
    @Override
    GenericContainer<?> create(RedisContainerConfig config) {
        return new GenericContainer<>("redis:" + config.getVersion())
                .withExposedPorts(6379);
    }
}
