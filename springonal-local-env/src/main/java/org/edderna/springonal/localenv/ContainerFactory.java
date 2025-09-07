package org.edderna.springonal.localenv;

import org.edderna.springonal.localenv.configuration.AbstractContainerConfig;
import org.edderna.springonal.localenv.configuration.mongodb.MongoContainerConfig;
import org.testcontainers.containers.GenericContainer;

public interface ContainerFactory<T extends AbstractContainerConfig> {
    GenericContainer create(T mongoContainerConfig);
}
