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

import org.edderna.springonal.localenv.configuration.mongodb.MongoContainerConfig;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

public class MongoContainerFactory extends ContainerFactory<MongoContainerConfig> {

    @Override
    public GenericContainer<?> create(MongoContainerConfig config) {
        GenericContainer<?> mongoDBContainer = new GenericContainer<>("mongo:" + config.getVersion())
                .withEnv("MONGO_INITDB_ROOT_USERNAME", config.getUsername())
                .withEnv("MONGO_INITDB_ROOT_PASSWORD", config.getPassword())
                .withEnv("MONGO_INITDB_DATABASE", config.getDbName())
                .withExposedPorts(27017);

        for (String script : config.getInitScripts()) {
            mongoDBContainer.withCopyFileToContainer(MountableFile.forClasspathResource(script),
                    "/docker-entrypoint-initdb.d/" + getScriptFileName(script));
        }

        return mongoDBContainer;
    }


}
