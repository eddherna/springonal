package org.edderna.springonal.localenv.configuration.postgre;

public class PostgreSQLConnectionStringBuilder {

    String host;
    Integer port;
    String dbName;

    String connectionStringPattern = "jdbc:postgresql://%s:%d/%s";


    public PostgreSQLConnectionStringBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public PostgreSQLConnectionStringBuilder setPort(Integer port) {
        this.port = port;
        return this;
    }

    public PostgreSQLConnectionStringBuilder setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public String build() {
        return String.format(connectionStringPattern, host, port, dbName);
    }
}
