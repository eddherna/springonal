package org.edderna.springonal.localenv.containers;

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
import org.edderna.springonal.localenv.container.RedisContainer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import redis.clients.jedis.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class RedisContainerTest {

    @ParameterizedTest
    @ValueSource(strings = {"8.2.1", "7.4.5", "6.2.19"})
    void shouldTestVersion(String version) {
        var toml = """
                version = "%s"
                username= "redis-test"
                password= "pass-test"
                """;

        RedisContainerConfig config = new RedisContainerConfig(new Toml().read(String.format(toml, version)));

        RedisContainer container = new RedisContainer(config);
        container.start();

        DefaultJedisClientConfig jedisConf = DefaultJedisClientConfig.builder()
                .user(container.getUsername())
                .password(container.getPassword())
                .build();

        String connectionString = "redis://localhost:" + container.getMappedPort(6379);
        int port = Integer.valueOf(connectionString.split(":")[2]);


        JedisPooled jedis = new JedisPooled(new HostAndPort("localhost", port), jedisConf);
        jedis.set("key", "value");

        assertThat(jedis.get("key")).isEqualTo("value");
        assertThat(container.getMappedPort(6379)).isNotNull();
        container.stop();
    }
}
