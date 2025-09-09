package org.edderna.springonal.localenv;

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
