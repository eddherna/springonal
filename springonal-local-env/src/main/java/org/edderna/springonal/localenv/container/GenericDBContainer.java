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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public abstract class GenericDBContainer extends org.testcontainers.containers.GenericContainer<GenericDBContainer> {

    private final List<String> initScripts;
    protected String username;
    protected String password;

    public GenericDBContainer(String imageName, String version, List<String> initScripts, String username, String password) {
        super(imageName + ":" + version);
        this.initScripts = initScripts;
        this.username = username;
        this.password = password;
        exposePorts();
    }

    @Override
    public void start() {
        super.start();
        try {
            customizeResource();
            if (!Objects.isNull(initScripts)) {
                initScripts.sort(String::compareTo);
                for (String scriptPath : initScripts) {
                    runScript(scriptPath);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void runScript(String scriptPath) throws IOException, InterruptedException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(scriptPath)) {
            String scriptContent = new String(Objects.requireNonNull(is).readAllBytes());
            runScriptContent(scriptContent);
        }
    }


    protected abstract void customizeResource() throws IOException, InterruptedException;

    protected void runScriptContent(String scriptContent) throws IOException, InterruptedException {
        // You must implement this method in the subclass if you declare a list of initialization scripts
    }

    protected abstract void exposePorts();

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
