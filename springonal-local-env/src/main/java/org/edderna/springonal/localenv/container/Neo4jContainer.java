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


import org.edderna.springonal.localenv.configuration.neo4j.Neo4jContainerConfig;

import java.io.IOException;
import java.util.UUID;

public class Neo4jContainer extends InitializedDbContainer<Neo4jContainerConfig> {

    private String neoUser = "neo4j";
    private String neoUserPassowrd = UUID.randomUUID().toString();

    private static final String ECHO_QUERY = "echo \"CREATE USER %s SET PASSWORD '%s' CHANGE NOT REQUIRED;\"";
    private static final String PIPE = "|";
    private static final String CYPHER_SHELL_LOGIN = "cypher-shell -u %s -p %s";

    public Neo4jContainer(Neo4jContainerConfig config) {
        super("neo4j", config);
        addEnv("NEO4J_AUTH", neoUser + "/" + neoUserPassowrd);
        if (config.hasConsole()) {
            addExposedPort(7474);
        }
    }

    @Override
    protected void customizeAfterStart(Neo4jContainerConfig config) throws IOException, InterruptedException {
        String command = String.join(" ", String.format(ECHO_QUERY, config.getUsername(), config.getPassword()),
                PIPE, String.format(CYPHER_SHELL_LOGIN, neoUser, neoUserPassowrd));
        execInContainer("/bin/sh", "-c", command);
    }

    @Override
    protected void runScriptContent(String scriptContent) throws IOException, InterruptedException {
        var echoCommand = "echo \"" + scriptContent + "\" | cypher-shell -u " + config.getUsername() +
                " -p " + config.getPassword();
        execInContainer("/bin/sh", "-c", echoCommand);
    }

    @Override
    protected void customizeContainerBeforeStart() {
        addExposedPort(7687);
    }
}
