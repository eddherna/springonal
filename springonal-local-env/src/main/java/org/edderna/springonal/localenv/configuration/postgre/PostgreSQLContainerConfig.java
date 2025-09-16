package org.edderna.springonal.localenv.configuration.postgre;

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
import org.edderna.springonal.localenv.configuration.AuthResourceContainerConfig;
import org.edderna.springonal.localenv.exception.MalformedEnviromentException;

import java.util.List;

public class PostgreSQLContainerConfig extends AuthResourceContainerConfig {
    private List<String> initScripts;
    private String dbName;

    public PostgreSQLContainerConfig(Toml toml) {
        super(toml);
        this.initScripts = toml.getList("initScripts", List.of());
        this.dbName = toml.getString("db-name", "test");

        validateInitScripts();
    }

    private void validateInitScripts() {
        for (String script : initScripts) {
            if (!script.endsWith(".sql")) {
                throw new MalformedEnviromentException("Init script '" + script + "' must have .sql extension");
            }
        }
    }

    public List<String> getInitScripts() {
        return initScripts;
    }

    public String getDbName() {
        return dbName;
    }
}
