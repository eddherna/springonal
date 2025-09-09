package org.edderna.springonal.localenv.configuration.mongodb;

/*-
 * #%L
 * springonal
 * %%
 * Copyright (C) 2025 Eduardo Daniel Hernandez
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
