package org.edderna.springonal.localenv.container;

import org.edderna.springonal.localenv.configuration.mongodb.MongoContainerConfig;
import org.edderna.springonal.localenv.utils.PathUtils;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.util.List;

public class MongoContainer extends GenericDBContainer {

    private String dbName;

    public MongoContainer(MongoContainerConfig config) {
        super("mongo", config.getVersion(), config.getInitScripts(),
                config.getUsername(), config.getPassword());
        withEnv("MONGO_INITDB_DATABASE", config.getDbName());

        dbName = config.getDbName();
        for (String script : config.getInitScripts()) {
            withCopyFileToContainer(MountableFile.forClasspathResource(script),
                    "/docker-entrypoint-initdb.d/" + PathUtils.getFilename(script));
        }
    }

    @Override
    protected void customizeResource() throws IOException, InterruptedException {
        var script = """
                    db.getSiblingDB('%s').createUser({
                        user: '%s',
                        pwd: '%s',
                        roles: [{
                            role: "readWrite",
                            db: "dbTest"
                        }]
                    })
                """;

        execInContainer("mongosh", "--eval", "\"" + String.format(script, dbName, username, password, dbName) + "\"");
    }

    @Override
    protected void exposePorts() {
        withExposedPorts(27017);
    }
}
