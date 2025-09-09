package org.edderna.springonal.localenv;

import com.moandjiezana.toml.Toml;
import org.assertj.core.api.Assertions;
import org.edderna.springonal.localenv.configuration.postgre.PostgreSQLContainerConfig;
import org.edderna.springonal.localenv.configuration.postgre.PostgreSQLConnectionStringBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.io.File;
import java.sql.*;

public class PostgreSQLContainerFactoryTest {

    private long executeQuery(String connectionString, String sql) throws SQLException {
        try (Connection conn = DriverManager.getConnection(connectionString); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0L;
        }
    }

    @Test
    void shouldExecuteCompletePostgreSQLContainer() throws SQLException {
        Toml toml = new Toml().read(new File("src/test/resources/postgre-tests-resources/complete-postgre.toml"));
        Toml postgreToml = toml.getTable("postgreSQL");
        PostgreSQLContainerConfig config = new PostgreSQLContainerConfig(postgreToml);

        PostgresContainerFactory factory = new PostgresContainerFactory();
        GenericContainer<?> container = factory.create(config);

        container.withExposedPorts(5432);
        container.start();

        try {
            String jdbcUrl = new PostgreSQLConnectionStringBuilder()
                    .setHost(container.getHost())
                    .setPort(container.getMappedPort(5432))
                    .setDbName(config.getDbName())
                    .build();


            String connectionString = jdbcUrl + "?user=adminPostgre&password=passwordPostgre";

            long testTableCount = executeQuery(connectionString, "SELECT COUNT(*) FROM test_table");
            Assertions.assertThat(testTableCount).isEqualTo(2L);

            long otherTableCount = executeQuery(connectionString, "SELECT COUNT(*) FROM other_test_table");
            Assertions.assertThat(otherTableCount).isEqualTo(2L);

        } finally {
            container.stop();
        }
    }

    @Test
    void shouldExecuteUnsortedInitScriptsSortedInPostgreSQLContainer() throws SQLException {
        Toml toml = new Toml().read(new File("src/test/resources/postgre-tests-resources/complete-postgre-unsorted-init-files.toml"));
        Toml postgreToml = toml.getTable("postgreSQL");
        PostgreSQLContainerConfig config = new PostgreSQLContainerConfig(postgreToml);

        PostgresContainerFactory factory = new PostgresContainerFactory();
        GenericContainer<?> container = factory.create(config);

        container.withExposedPorts(5432);
        container.start();

        try {
            String jdbcUrl = new PostgreSQLConnectionStringBuilder()
                    .setHost(container.getHost())
                    .setPort(container.getMappedPort(5432))
                    .setDbName("testdbPostgre")
                    .build();

            String connectionString = jdbcUrl + "?user=adminPostgre&password=passwordPostgre";

            long testTableCount = executeQuery(connectionString, "SELECT COUNT(*) FROM test_table");
            Assertions.assertThat(testTableCount).isEqualTo(2L);

            long otherTableCount = executeQuery(connectionString, "SELECT COUNT(*) FROM other_test_table");
            Assertions.assertThat(otherTableCount).isEqualTo(2L);

        } finally {
            container.stop();
        }
    }

    @Test
    void shouldExecutePostgreSQLContainerWithDefaultAuth() throws SQLException {
        Toml toml = new Toml().read(new File("src/test/resources/postgre-tests-resources/default-security-postgre.toml"));
        Toml postgreToml = toml.getTable("postgreSQL");
        PostgreSQLContainerConfig config = new PostgreSQLContainerConfig(postgreToml);

        PostgresContainerFactory factory = new PostgresContainerFactory();
        GenericContainer<?> container = factory.create(config);

        container.withExposedPorts(5432);
        container.start();

        try {
            String jdbcUrl = new PostgreSQLConnectionStringBuilder()
                    .setHost(container.getHost())
                    .setPort(container.getMappedPort(5432))
                    .setDbName("testdbPostgre")
                    .build();

            String connectionString = jdbcUrl + "?user=admin&password=password";

            long testTableCount = executeQuery(connectionString, "SELECT 1;");
            Assertions.assertThat(testTableCount).isEqualTo(1L);
        } finally {
            container.stop();
        }
    }

    @Test
    void shouldExecuteCompletePostgreSQLContainerNoInit() throws SQLException {
        Toml toml = new Toml().read(new File("src/test/resources/postgre-tests-resources/no-init-postgre.toml"));
        Toml postgreToml = toml.getTable("postgreSQL");
        PostgreSQLContainerConfig config = new PostgreSQLContainerConfig(postgreToml);

        PostgresContainerFactory factory = new PostgresContainerFactory();
        GenericContainer<?> container = factory.create(config);

        container.withExposedPorts(5432);
        container.start();

        try {
            String jdbcUrl = new PostgreSQLConnectionStringBuilder()
                    .setHost(container.getHost())
                    .setPort(container.getMappedPort(5432))
                    .setDbName(config.getDbName())
                    .build();


            String connectionString = jdbcUrl + "?user=adminPostgre&password=passwordPostgre";

            long testTableCount = executeQuery(connectionString, "SELECT COUNT(*) \n" +
                    "FROM information_schema.tables \n" +
                    "WHERE table_schema = 'public' \n" +
                    "AND table_type = 'BASE TABLE'");
            Assertions.assertThat(testTableCount).isEqualTo(0L);


        } finally {
            container.stop();
        }
    }

}
