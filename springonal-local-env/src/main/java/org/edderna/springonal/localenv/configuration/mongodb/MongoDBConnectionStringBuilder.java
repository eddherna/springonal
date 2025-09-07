package org.edderna.springonal.localenv.configuration.mongodb;

public class MongoDBConnectionStringBuilder {

    private static final String URL_FORMAT = "mongodb://%s:%s@%s:%d/%s?authSource=admin";

    String username;
    String password;
    String host;
    Integer port;
    String dbName;

    public MongoDBConnectionStringBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public MongoDBConnectionStringBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public MongoDBConnectionStringBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public MongoDBConnectionStringBuilder setPort(Integer port) {
        this.port = port;
        return this;
    }

    public MongoDBConnectionStringBuilder setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public String build() {
        return String.format(URL_FORMAT,
                username,
                password,
                host,
                port,
                dbName);
    }

}
