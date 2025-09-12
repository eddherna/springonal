package org.edderna.springonal.localenv.configuration.scylla;

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

import java.util.List;

public class ScyllaContainerConfig extends AuthResourceContainerConfig {

    private String keyspace;
    private List<String> initScripts;

    public ScyllaContainerConfig(Toml toml) {
        super(toml);
        keyspace = toml.getString("keyspace", "test");
        initScripts = toml.getList("init-scripts", List.of());
    }

    public String getKeyspace() {
        return keyspace;
    }

    public List<String> getInitScripts() {
        return initScripts;
    }

}
