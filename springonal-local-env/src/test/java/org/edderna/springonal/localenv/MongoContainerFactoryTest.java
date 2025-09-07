package org.edderna.springonal.localenv;

import com.moandjiezana.toml.Toml;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.edderna.springonal.localenv.configuration.mongodb.MongoContainerConfig;
import org.edderna.springonal.localenv.configuration.mongodb.MongoDBConnectionStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoContainerFactoryTest {

    private MongoClient mongoFullConfigInitTest(String mongoFile) throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/mongo-tests-resources/" + mongoFile)) {
            Toml toml = new Toml().read(is);

            MongoContainerFactory mcf = new MongoContainerFactory();

            MongoContainerConfig config = new MongoContainerConfig(toml.getTable("mongo"));
            GenericContainer mc = mcf.create(config);
            mc.start();

            MongoClient client = getMongoClient(config, mc);
            return client;
        }
    }

    @Test
    void shouldStartContainerWithMongo4Options() throws IOException {
        String mongoFile = "complete-mongo-4.toml";
        MongoClient client = mongoFullConfigInitTest(mongoFile);
        assertThat(client.getDatabase("testDbMongo").getCollection("users").countDocuments())
                .isEqualTo(4);

        assertThat(client.getDatabase("testDbMongo").getCollection("products").countDocuments())
                .isEqualTo(2);
    }

    @Test
    void shouldStartContainerWithMongo5Options() throws IOException {
        String mongoFile = "complete-mongo-5.toml";
        MongoClient client = mongoFullConfigInitTest(mongoFile);
        assertThat(client.getDatabase("testDbMongo").getCollection("users").countDocuments())
                .isEqualTo(4);

        assertThat(client.getDatabase("testDbMongo").getCollection("products").countDocuments())
                .isEqualTo(2);

    }

    @Test
    void shouldStartContainerWithMongo6Options() throws IOException {
        String mongoFile = "complete-mongo-6.toml";
        MongoClient client = mongoFullConfigInitTest(mongoFile);
        assertThat(client.getDatabase("testDbMongo").getCollection("users").countDocuments())
                .isEqualTo(4);

        assertThat(client.getDatabase("testDbMongo").getCollection("products").countDocuments())
                .isEqualTo(2);
    }

    @Test
    void shouldStartContainerWithMongo7Options() throws IOException {
        String mongoFile = "complete-mongo-7.toml";
        MongoClient client = mongoFullConfigInitTest(mongoFile);
        assertThat(client.getDatabase("testDbMongo").getCollection("users").countDocuments())
                .isEqualTo(4);

        assertThat(client.getDatabase("testDbMongo").getCollection("products").countDocuments())
                .isEqualTo(2);
    }

    @Test
    void shouldStartContainerWithoutInitScripts() throws IOException {
        String mongoFile = "no-init-mongo.toml";
        MongoClient client = mongoFullConfigInitTest(mongoFile);
        assertThat(client.getDatabase("testDbMongo").listCollections())
                .hasSize(0);
    }

    @Test
    void shouldStartContainerWithDefaultUserAndPassword() throws IOException {
        String mongoFile = "default-security-mongo.toml";
        try (InputStream is = getClass().getResourceAsStream("/mongo-tests-resources/" + mongoFile)) {
            Toml toml = new Toml().read(is);

            MongoContainerFactory mcf = new MongoContainerFactory();

            MongoContainerConfig config = new MongoContainerConfig(toml.getTable("mongo"));
            GenericContainer mc = mcf.create(config);
            List<String> envs = mc.getEnv();
            assertThat(envs).contains("MONGO_INITDB_ROOT_USERNAME=admin", "MONGO_INITDB_ROOT_PASSWORD=password");
        }

    }

    @Test
    void shouldStartContainerWithDefaultDbName() throws IOException {
        String mongoFile = "no-db-name-mongo.toml";
        try (InputStream is = getClass().getResourceAsStream("/mongo-tests-resources/" + mongoFile)) {
            Toml toml = new Toml().read(is);

            MongoContainerFactory mcf = new MongoContainerFactory();

            MongoContainerConfig config = new MongoContainerConfig(toml.getTable("mongo"));
            GenericContainer mc = mcf.create(config);
            List<String> envs = mc.getEnv();
            assertThat(envs).contains("MONGO_INITDB_DATABASE=test");
        }

    }


    private static MongoClient getMongoClient(MongoContainerConfig config, GenericContainer mc) {
        MongoClient client = MongoClients.create(
                new MongoDBConnectionStringBuilder()
                        .setDbName(config.getDbName())
                        .setUsername(config.getUsername())
                        .setPassword(config.getPassword())
                        .setHost(mc.getHost())
                        .setPort(mc.getMappedPort(27017))
                        .build()
        );
        return client;
    }


}
