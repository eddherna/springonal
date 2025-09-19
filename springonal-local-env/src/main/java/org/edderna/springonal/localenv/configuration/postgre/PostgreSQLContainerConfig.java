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
import org.edderna.springonal.localenv.configuration.InitializableDatabaseContainerConfig;
import org.edderna.springonal.localenv.exception.MalformedEnviromentException;

public class PostgreSQLContainerConfig extends InitializableDatabaseContainerConfig {
    private String dbName;

    public PostgreSQLContainerConfig(Toml toml) {
        super(toml);
        this.dbName = toml.getString("db-name", "test").toLowerCase();

        validateInitScripts();
    }

    private void validateInitScripts() {
        for (String script : initScripts) {
            if (!script.endsWith(".sql")) {
                throw new MalformedEnviromentException("Init script '" + script + "' must have .sql extension");
            }
        }
    }

    public String getDbName() {
        return dbName;
    }
}
