package org.edderna.springonal.localenv.configuration.mongodb;

import com.moandjiezana.toml.Toml;
import org.edderna.springonal.localenv.configuration.AbstractContainerConfig;

import java.util.List;

public class MongoContainerConfig extends AbstractContainerConfig {
    String username;
    String password;
    String dbName;
    String[] initScripts;

    public MongoContainerConfig(Toml toml) {
        super(toml);
        this.username = toml.getString("username", "admin");
        this.password = toml.getString("password", "password");
        this.dbName = toml.getString("dbName", "test");
        this.initScripts = toml.getList("initScripts", List.of()).toArray(new String[0]);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDbName() {
        return dbName;
    }

    public String[] getInitScripts() {
        return initScripts;
    }
}
