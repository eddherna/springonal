package org.edderna.springonal.localenv;

import org.edderna.springonal.localenv.configuration.mongodb.MongoContainerConfig;
import org.edderna.springonal.localenv.configuration.postgre.PostgreSQLContainerConfig;
import org.testcontainers.containers.GenericContainer;

public class PostgreContainerFactory implements ContainerFactory<PostgreSQLContainerConfig> {


    @Override
    public GenericContainer create(PostgreSQLContainerConfig mongoContainerConfig) {
        return null;
    }
}
