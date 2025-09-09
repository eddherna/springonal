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
