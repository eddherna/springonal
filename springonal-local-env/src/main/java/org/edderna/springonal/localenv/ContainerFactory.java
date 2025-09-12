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

import org.edderna.springonal.localenv.configuration.AbstractContainerConfig;
import org.testcontainers.containers.GenericContainer;

public abstract class ContainerFactory<T extends AbstractContainerConfig> {
    abstract GenericContainer<?> create(T config);

    protected String getScriptFileName(String scriptPath) {
        return scriptPath.substring(scriptPath.lastIndexOf('/') + 1);
    }
}
