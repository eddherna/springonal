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
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.concurrent.Future;

public abstract class GenericSpringonalContainer<T extends AbstractContainerConfig> extends GenericContainer<GenericSpringonalContainer<T>> {
    protected T config;

    public GenericSpringonalContainer(String imageName, T config) {
        super(imageName + ":" + config.getVersion());
        this.config = config;
        customizeContainerBeforeStart();
    }

    public GenericSpringonalContainer(Future<String> image, T config) {
        super(image);
        this.config = config;
        customizeContainerBeforeStart();
    }

    public GenericSpringonalContainer(DockerImageName dockerImageName, T config) {
        super(dockerImageName);
        this.config = config;
        customizeContainerBeforeStart();
    }

    @Override
    public void start() {
        super.start();
        try {
            customizeAfterStart(config);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void customizeAfterStart(T config) throws IOException, InterruptedException {
    }

    protected abstract void customizeContainerBeforeStart();
}
