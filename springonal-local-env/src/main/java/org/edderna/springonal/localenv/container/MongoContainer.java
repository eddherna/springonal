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

    public static final String FIXED_DB_REFERENCE = "db = db.getSiblingDB('%s');";

    public MongoContainer(MongoContainerConfig config) {
        super("mongo", config);
        withEnv("MONGO_INITDB_DATABASE", config.getDbName());
    }

    @Override
    protected void runScriptContent(String scriptContent) throws IOException, InterruptedException {
        String newScript = String.format(FIXED_DB_REFERENCE, config.getDbName()) + scriptContent;
        execInContainer("mongosh", "-u", config.getUsername(), "-p", config.getPassword(), "--authenticationDatabase",
                config.getDbName(), "--eval", newScript);
    }

    @Override
    protected void customizeAfterStart(MongoContainerConfig config) throws IOException, InterruptedException {
        execInContainer("mongosh", "--eval", "\"" + String.format(CREATE_USER_QUERY, config.getDbName(),
                config.getUsername(), config.getPassword(), config.getDbName()) + "\"");
    }

    @Override
    protected void customizeContainerBeforeStart() {
        withExposedPorts(27017);
    }
}
