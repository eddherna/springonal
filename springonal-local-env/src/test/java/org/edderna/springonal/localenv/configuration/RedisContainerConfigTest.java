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
