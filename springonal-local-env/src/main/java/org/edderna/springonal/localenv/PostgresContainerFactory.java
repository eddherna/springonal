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

import org.edderna.springonal.localenv.configuration.postgre.PostgreSQLContainerConfig;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

public class PostgresContainerFactory extends ContainerFactory<PostgreSQLContainerConfig> {

    @Override
    public GenericContainer<?> create(PostgreSQLContainerConfig config) {
        GenericContainer<?> container = new GenericContainer<>("postgres:" + config.getVersion())
                .withEnv("POSTGRES_USER", config.getUsername())
                .withEnv("POSTGRES_PASSWORD", config.getPassword())
                .withEnv("POSTGRES_DB", config.getDbName());

        for (String script : config.getInitScripts()) {
            container.withCopyFileToContainer(MountableFile.forClasspathResource(script),
                    "/docker-entrypoint-initdb.d/" + getScriptFileName(script));
        }

        return container;
    }
}
