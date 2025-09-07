package org.edderna.springonal.localenv.configuration;


import com.moandjiezana.toml.Toml;
import org.edderna.springonal.localenv.configuration.mongodb.MongoContainerConfig;


public class LocalEnvironment {

    private MongoContainerConfig mongoContainerConfig;

    public LocalEnvironment(Toml toml) {
        this.mongoContainerConfig = new MongoContainerConfig(toml.getTable("mongo"));
    }

    public MongoContainerConfig getMongoContainerConfig() {
        return mongoContainerConfig;
    }
}
