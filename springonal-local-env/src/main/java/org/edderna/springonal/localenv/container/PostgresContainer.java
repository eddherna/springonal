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

import org.edderna.springonal.localenv.configuration.postgre.PostgreSQLContainerConfig;

import java.io.IOException;

public class PostgresContainer extends InitializedDbContainer<PostgreSQLContainerConfig> {

    private static final String CREATE_USER = "CREATE USER %s;";
    private static final String CREATE_DATABASE = "CREATE DATABASE %s;";
    private static final String ADD_PASSWORD = "ALTER USER %s WITH ENCRYPTED PASSWORD '%s';";
    private static final String GRANT_DATABASE = "GRANT ALL PRIVILEGES ON DATABASE %s TO %s;";
    private static final String GRANT_SCHEMA = "GRANT CREATE, USAGE ON SCHEMA public TO %s;";
    private static final String GRANT_TABLES = "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO %s;";
    private static final String GRANT_SEQUENCES = "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO %s;";

    public PostgresContainer(PostgreSQLContainerConfig config) {
        super("postgres", config);
        withEnv("POSTGRES_PASSWORD", "admin");
    }


    @Override
    protected void runScriptContent(String scriptContent) throws IOException, InterruptedException {
        String cmd = String.format("PGPASSWORD=%s psql -U %s -d %s << EOF \n", config.getPassword(),
                config.getUsername(), config.getDbName()) + scriptContent + "EOF";
        execInContainer("/bin/sh", "-c", cmd);
    }

    @Override
    protected void customizeAfterStart(PostgreSQLContainerConfig config) throws IOException, InterruptedException {
        execInContainer("psql", "-U", "postgres", "-c", String.format(CREATE_USER, config.getUsername()));
        execInContainer("psql", "-U", "postgres", "-c", String.format(CREATE_DATABASE, config.getDbName()));
        execInContainer("psql", "-U", "postgres", "-c", String.format(ADD_PASSWORD, config.getUsername(), config.getPassword()));
        execInContainer("psql", "-U", "postgres", "-c", String.format(GRANT_DATABASE, config.getDbName(), config.getUsername()));
        execInContainer("psql", "-U", "postgres", "-d", config.getDbName(), "-c", String.format(GRANT_SCHEMA, config.getUsername()));
        execInContainer("psql", "-U", "postgres", "-d", config.getDbName(), "-c", String.format(GRANT_TABLES, config.getUsername()));
        execInContainer("psql", "-U", "postgres", "-d", config.getDbName(), "-c", String.format(GRANT_SEQUENCES, config.getUsername()));
    }

    @Override
    protected void exposePorts() {
        withExposedPorts(5432);
    }
}
