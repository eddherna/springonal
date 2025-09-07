package org.edderna.springonal.localenv;

import org.edderna.springonal.localenv.configuration.mongodb.MongoContainerConfig;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

public class MongoContainerFactory {

    public GenericContainer create(MongoContainerConfig mongoContainerConfig) {
        GenericContainer mongoDBContainer = new GenericContainer("mongo:" + mongoContainerConfig.getVersion())
                .withEnv("MONGO_INITDB_ROOT_USERNAME", mongoContainerConfig.getUsername())
                .withEnv("MONGO_INITDB_ROOT_PASSWORD", mongoContainerConfig.getPassword())
                .withEnv("MONGO_INITDB_DATABASE", mongoContainerConfig.getDbName())
                .withExposedPorts(27017);

        for (String script : mongoContainerConfig.getInitScripts()) {
            mongoDBContainer.withCopyFileToContainer(MountableFile.forClasspathResource(script),
                    "/docker-entrypoint-initdb.d/" + getScriptFileName(script));
        }

        return mongoDBContainer;
    }

    private String getScriptFileName(String scriptPath) {
        return scriptPath.substring(scriptPath.lastIndexOf('/') + 1);
    }
}
