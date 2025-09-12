package org.edderna.springonal.localenv;

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

import org.edderna.springonal.localenv.configuration.LocalEnvironment;
import org.edderna.springonal.localenv.configuration.mongodb.MongoContainerConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalEnvironmentFactoryTest {

    @Test
    public void shouldReturnLocalEnvironmentWithOnlyMongoContainer() throws Exception {
        LocalEnvironmentFactory factory = new LocalEnvironmentFactory();
        LocalEnvironment env = factory.create("/mongo-tests-resources/complete-mongo-4.toml");
        assertThat(env).hasAllNullFieldsOrPropertiesExcept("mongoContainerConfig");
        MongoContainerConfig mongo = env.getMongoContainerConfig();
        assertThat(mongo).isNotNull();
        assertThat(mongo.getVersion()).isEqualTo("4.4");
        assertThat(mongo.getUsername()).isEqualTo("adminMongo");
        assertThat(mongo.getPassword()).isEqualTo("passwordMongo");
        assertThat(mongo.getDbName()).isEqualTo("testDbMongo");
        assertThat(mongo.getInitScripts()).containsExactly(
                "/mongo-tests-resources/mongo-test-init-scripts/collection-1.js",
                "/mongo-tests-resources/mongo-test-init-scripts/collection-2.js"
        );
    }

}
