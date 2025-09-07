package org.edderna.springonal.localenv.configuration.mongodb;

import com.moandjiezana.toml.Toml;

import java.util.List;

public class MongoContainerConfig {

    String version;
    String username;
    String password;
    String dbName;
    String[] initScripts;

    public MongoContainerConfig(Toml toml) {
        this.version = toml.getString("version");
        if (this.version == null) {
            throw new NullPointerException("Version definition cannot be null.");
        }
        this.username = toml.getString("username", "admin");
        this.password = toml.getString("password", "password");
        this.dbName = toml.getString("dbName", "test");
        this.initScripts = toml.getList("initScripts", List.of()).toArray(new String[0]);
    }

    public String getVersion() {
        return version;
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
