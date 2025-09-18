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

import org.edderna.springonal.localenv.configuration.mongodb.MongoContainerConfig;
import org.edderna.springonal.localenv.utils.PathUtils;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;

public class MongoContainer extends InitializedDbContainer<MongoContainerConfig> {

    public static final String CREATE_USER_QUERY = """
                db.getSiblingDB('%s').createUser({
                    user: '%s',
                    pwd: '%s',
                    roles: [{
                        role: "readWrite",
                        db: "%s"
                    }]
                })
            """;

    public MongoContainer(MongoContainerConfig config) {
        super("mongo", config);
        withEnv("MONGO_INITDB_DATABASE", config.getDbName());
        for (String script : config.getInitScripts()) {
            withCopyFileToContainer(MountableFile.forClasspathResource(script),
                    "/docker-entrypoint-initdb.d/" + PathUtils.getFilename(script));
        }
    }

    @Override
    protected void customizeAfterStart(MongoContainerConfig config) throws IOException, InterruptedException {
        execInContainer("mongosh", "--eval", "\"" + String.format(CREATE_USER_QUERY, config.getDbName(),
                config.getUsername(), config.getPassword(), config.getDbName()) + "\"");
    }

    @Override
    protected void exposePorts() {
        withExposedPorts(27017);
    }
}
