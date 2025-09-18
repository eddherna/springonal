package org.edderna.springonal.localenv.configuration;

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
import org.edderna.springonal.localenv.configuration.scylla.ScyllaContainerConfigDb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbAuthContainerConfig extends AbstractContainerConfig {

    public static final String DEFAULT_USER = "test";
    public static final String DEFAULT_PASS = "test";
    private static Logger logger = LoggerFactory.getLogger(ScyllaContainerConfigDb.class);

    private final String username;
    private final String password;

    public DbAuthContainerConfig(Toml toml) {
        super(toml);
        if (toml.contains("username") && toml.contains("password")) {
            username = toml.getString("username", DEFAULT_USER);
            password = toml.getString("password", DEFAULT_PASS);
        } else {
            username = DEFAULT_USER;
            password = DEFAULT_PASS;
        }

        if ((toml.contains("username") && !toml.contains("password")) ||
                (!toml.contains("username") && toml.contains("password"))) {
            logger.warn("You must define both username and password to use this configuration. Default values will be used instead.");
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
