package org.edderna.springonal.localenv.configuration.postgre;

import com.moandjiezana.toml.Toml;
import org.edderna.springonal.localenv.configuration.AbstractContainerConfig;

import java.util.List;

public class PostgreSQLContainerConfig extends AbstractContainerConfig {
    private boolean readWrite;
    private List<String> initScripts;
    private String username;
    private String password;
    private String dbName;

    public PostgreSQLContainerConfig(Toml toml) {
        super(toml);
        this.readWrite = toml.getBoolean("readWrite");
        this.initScripts = toml.getList("initScripts");
        this.username = toml.getString("username");
        this.password = toml.getString("password");
        this.dbName = toml.getString("dbName");
    }

    public boolean isReadWrite() {
        return readWrite;
    }

    public List<String> getInitScripts() {
        return initScripts;
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
}
