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

import com.github.dockerjava.api.model.Capability;
import com.github.dockerjava.api.model.Ulimit;
import org.edderna.springonal.localenv.configuration.scylla.ScyllaContainerConfigDb;

import java.io.IOException;

public class ScyllaContainer extends GenericDbInitializedContainer<ScyllaContainerConfigDb> {


    private static final String CREATE_KEYSPACE = "CREATE KEYSPACE IF NOT EXISTS %s " +
            "WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};";

    private static final String CREATE_USER = "CREATE ROLE IF NOT EXISTS %s WITH PASSWORD = '%s' AND LOGIN = true;";
    private static final String GRANT_PERMISSIONS = "GRANT ALL PERMISSIONS ON KEYSPACE %s TO %s;";

    private String keyspace;


    public ScyllaContainer(ScyllaContainerConfigDb config) {
        super("scylladb/scylla", config);
        this.keyspace = config.getKeyspace();
        withCreateContainerCmdModifier(it -> {
            it.withCmd("--authenticator", "PasswordAuthenticator", "--authorizer", "CassandraAuthorizer",
                    "--disable-version-check", "--skip-wait-for-gossip-to-settle", "0");

            it.getHostConfig()
                    .withShmSize(256 * 1024 * 1024L) // 256MB shared memory
                    .withCapAdd(Capability.SYS_NICE) // Capacidad para ajustar prioridades
                    .withUlimits(new Ulimit[]{
                            new Ulimit("memlock", -1L, -1L),
                            new Ulimit("nofile", 900000L, 900000L)
                    });

            it.withCmd("--smp", "1",
                    "--memory", "750M",
                    "--overprovisioned", "1",
                    "--api-address", "0.0.0.0",
                    "--developer-mode", "1");
        });


    }

    @Override
    protected void customizeResource() throws IOException, InterruptedException {
        this.execInContainer("cqlsh", "-u", "cassandra", "-p", "cassandra", "-e", String.format(CREATE_KEYSPACE, keyspace));
        this.execInContainer("cqlsh", "-u", "cassandra", "-p", "cassandra", "-e", String.format(CREATE_USER, username, password));
        this.execInContainer("cqlsh", "-u", "cassandra", "-p", "cassandra", "-e", String.format(GRANT_PERMISSIONS, keyspace, username));
    }

    @Override
    protected void exposePorts() {
        withExposedPorts(9042);
    }

    @Override
    protected void runScriptContent(String scriptContent) throws IOException, InterruptedException {
        this.execInContainer("cqlsh", "-u", username, "-p", password, "-e", scriptContent);
    }
}
