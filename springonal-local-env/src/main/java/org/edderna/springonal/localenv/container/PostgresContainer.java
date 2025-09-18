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

import org.edderna.springonal.localenv.configuration.postgre.PostgreSQLContainerConfigDb;
import org.edderna.springonal.localenv.utils.PathUtils;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;

public class PostgresContainer extends GenericDbInitializedContainer<PostgreSQLContainerConfigDb> {
    public PostgresContainer(PostgreSQLContainerConfigDb config) {
        super("postgres", config);

        withEnv("POSTGRES_USER", config.getUsername());
        withEnv("POSTGRES_PASSWORD", config.getPassword());
        withEnv("POSTGRES_DB", config.getDbName());


        for (String script : config.getInitScripts()) {
            withCopyFileToContainer(MountableFile.forClasspathResource(script),
                    "/docker-entrypoint-initdb.d/" + PathUtils.getFilename(script));
        }
    }

    @Override
    protected void customizeResource() throws IOException, InterruptedException {
    }

    @Override
    protected void exposePorts() {
        withExposedPorts(5432);
    }
}
