package org.edderna.springonal.localenv.container;

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

import java.io.IOException;

public abstract class GenericDBContainer<T extends AbstractContainerConfig> extends GenericContainer<GenericDBContainer<T>> {
    protected String username;
    protected String password;

    protected T config;

    public GenericDBContainer(String imageName, T config) {
        super(imageName + ":" + config.getVersion());
        this.config = config;
        exposePorts();
    }

    @Override
    public void start() {
        super.start();
        try {
            customizeResource(config);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void customizeResource(T config) throws IOException, InterruptedException;

    protected void runScriptContent(String scriptContent) throws IOException, InterruptedException {
        // You must implement this method in the subclass if you declare a list of initialization scripts
    }

    protected abstract void exposePorts();
}
