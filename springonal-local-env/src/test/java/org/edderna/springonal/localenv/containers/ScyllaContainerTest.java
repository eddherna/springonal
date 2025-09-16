package org.edderna.springonal.localenv.containers;

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


import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.moandjiezana.toml.Toml;
import org.edderna.springonal.localenv.configuration.scylla.ScyllaContainerConfig;
import org.edderna.springonal.localenv.container.ScyllaContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.InetSocketAddress;

import static org.assertj.core.api.Assertions.assertThat;


public class ScyllaContainerTest {

    /**
     * Versiones <6 son muy lentas  "5.4.5", "5.2.13"
     *
     */
    @ParameterizedTest
    @ValueSource(strings = {"2025.3", "6.2.2"})
    void shouldCreateContainerAndRunScript(String version) {
        String toml = """
                version="%s"
                username="test"
                password="test"
                init-scripts=["scylla-scripts/001-init.cql", "scylla-scripts/002-init.cql"]
                keyspace="test_keyspace"
                """;
        ScyllaContainerConfig config = new ScyllaContainerConfig(new Toml().read(String.format(toml, version)));

        ScyllaContainer container = new ScyllaContainer(config);

        container.start();

        var session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("localhost", container.getMappedPort(9042)))
                .withLocalDatacenter("datacenter1")
                .withKeyspace("test_keyspace")
                .build();

        String queryUsers = "SELECT COUNT(*) FROM %s.users;";
        String queryProducts = "SELECT COUNT(*) FROM %s.products;";

        ResultSet r1 = session.execute(String.format(queryUsers, "test_keyspace"));
        ResultSet r2 = session.execute(String.format(queryProducts, "test_keyspace"));

        long countUsers = r1.one().getLong(0);
        long countProducts = r2.one().getLong(0);

        assertThat(countUsers).isEqualTo(2);
        assertThat(countProducts).isEqualTo(1);

        container.stop();
    }

    @Test
    void shouldCreateContainerAndSortScripts() {
        String toml = """
                version="2025.3"
                username="test"
                password="test"
                init-scripts=["scylla-scripts/002-init.cql", "scylla-scripts/001-init.cql"]
                keyspace="test_keyspace"
                """;

        ScyllaContainerConfig config = new ScyllaContainerConfig(new Toml().read(toml));

        ScyllaContainer container = new ScyllaContainer(config);

        container.start();

        var session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("localhost", container.getMappedPort(9042)))
                .withLocalDatacenter("datacenter1")
                .withKeyspace("test_keyspace")
                .build();

        String queryUsers = "SELECT COUNT(*) FROM %s.users;";
        String queryProducts = "SELECT COUNT(*) FROM %s.products;";

        ResultSet r1 = session.execute(String.format(queryUsers, "test_keyspace"));
        ResultSet r2 = session.execute(String.format(queryProducts, "test_keyspace"));

        assertThat(r1.one().getLong(0)).isEqualTo(2);
        assertThat(r2.one().getLong(0)).isEqualTo(1);
        assertThat(container.getMappedPort(9042)).isNotNull();
    }
}
