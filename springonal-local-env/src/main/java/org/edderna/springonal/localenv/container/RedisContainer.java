package org.edderna.springonal.localenv.container;

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

import org.edderna.springonal.localenv.configuration.redis.RedisContainerConfig;


import java.io.IOException;

public class RedisContainer extends GenericDbContainer<RedisContainerConfig> {


    public RedisContainer(RedisContainerConfig config) {
        super("redis", config);
        this.withExposedPorts(6379);
    }

    @Override
    protected void customizeAfterStart(RedisContainerConfig config) throws IOException, InterruptedException {
        this.execInContainer("/bin/sh", "-c", "redis-cli ACL SETUSER " + config.getUsername() + " ON '>" + config.getPassword() + "' +@all ~*");
        // Remove annonymous login
        this.execInContainer("/bin/sh", "-c", "redis-cli ACL SETUSER default OFF");
    }

    @Override
    protected void exposePorts() {
        this.withExposedPorts(6379);
    }
}
