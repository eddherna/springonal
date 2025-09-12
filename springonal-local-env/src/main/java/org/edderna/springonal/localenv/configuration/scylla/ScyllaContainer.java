package org.edderna.springonal.localenv.configuration.scylla;

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

import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class ScyllaContainer extends GenericContainer<ScyllaContainer> {


    private static final String CREATE_KEYSPACE = "CREATE KEYSPACE IF NOT EXISTS %s " +
            "WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};";

    private static final String CREATE_USER = "CREATE ROLE IF NOT EXISTS %s WITH PASSWORD = '%s' AND LOGIN = true;";
    private static final String GRANT_PERMISSIONS = "GRANT ALL PERMISSIONS ON KEYSPACE %s TO %s;";

    private String keyspace;
    private String username;
    private String password;
    private List<String> initScripts;


    public ScyllaContainer(ScyllaContainerConfig config) {
        super("scylladb/scylla:" + config.getVersion());
        this.keyspace = config.getKeyspace();
        this.username = config.getUsername();
        this.password = config.getPassword();
        this.initScripts = config.getInitScripts();
        withCreateContainerCmdModifier(it ->
                it.withCmd("--authenticator", "PasswordAuthenticator", "--authorizer", "CassandraAuthorizer",
                        "--disable-version-check", "--skip-wait-for-gossip-to-settle", "0"));
        withExposedPorts(9042);
    }

    @Override
    public void start() {
        super.start();
        try {
            this.execInContainer("cqlsh", "-u", "cassandra", "-p", "cassandra", "-e", String.format(CREATE_KEYSPACE, keyspace));
            this.execInContainer("cqlsh", "-u", "cassandra", "-p", "cassandra", "-e", String.format(CREATE_USER, username, password));
            this.execInContainer("cqlsh", "-u", "cassandra", "-p", "cassandra", "-e", String.format(GRANT_PERMISSIONS, keyspace, username));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        runScriptsIfApply();
    }

    public ScyllaContainer withKeyspace(String keyspace) {
        this.keyspace = keyspace;
        return this;
    }

    public ScyllaContainer withUsername(String username) {
        this.username = username;
        return this;
    }

    public ScyllaContainer withPassword(String password) {
        this.password = password;
        return this;
    }

    public ScyllaContainer withInitScripts(List<String> initScripts) {
        this.initScripts = initScripts;
        return this;
    }

    private void runScriptsIfApply() {
        if (initScripts != null) {
            initScripts.sort(String::compareTo);
            for (String scriptPath : initScripts) {
                try (InputStream is = getClass().getClassLoader().getResourceAsStream(scriptPath)) {
                    String scriptContent = new String(Objects.requireNonNull(is).readAllBytes());
                    this.execInContainer("cqlsh", "-u", username, "-p", password, "-e", scriptContent);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException("Failed to execute init script: " + scriptPath, e);
                } catch (NullPointerException e) {
                    throw new RuntimeException("Failed to load " + scriptPath);
                }
            }
        }
    }

}
