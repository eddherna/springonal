package org.edderna.springonal.localenv.containers;

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

import org.edderna.springonal.localenv.configuration.InitializedDbContainerConfig;
import org.edderna.springonal.localenv.container.GenericDBContainer;
import org.edderna.springonal.localenv.container.GenericDbInitializedContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GenericDBContainerTest {

    @Test
    void shouldThrowExceptionWhenCustomizeThrowIOException() throws IOException, InterruptedException {
        InitializedDbContainerConfig config = mock(InitializedDbContainerConfig.class);
        when(config.getVersion()).thenReturn("latest");

        GenericDBContainer container = new GenericDbInitializedContainer("hello-world", config) {
            @Override
            protected void customizeResource() throws IOException, InterruptedException {
                throw new IOException();
            }

            @Override
            protected void exposePorts() {
            }
        };


        container = spy(container);

        doThrow(new IOException("Script execution failed"))
                .when(container).execInContainer(any(String[].class));

        assertThatThrownBy(container::start)
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IOException.class);
        container.stop();
    }

    @Test
    void shouldThrowExceptionWhenExecInContainerThrowInterruptedException() throws IOException, InterruptedException {
        InitializedDbContainerConfig config = mock(InitializedDbContainerConfig.class);
        when(config.getVersion()).thenReturn("latest");

        GenericDBContainer container = new GenericDbInitializedContainer("hello-world", config) {
            @Override
            protected void customizeResource() throws IOException, InterruptedException {
                throw new InterruptedException();
            }

            @Override
            protected void exposePorts() {
            }
        };


        container = spy(container);

        doThrow(new IOException("Script execution failed"))
                .when(container).execInContainer(any(String[].class));

        assertThatThrownBy(container::start)
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(InterruptedException.class);

        container.stop();
    }

    @Test
    void shouldCallRunScriptForEveryScript() {



        AtomicInteger times = new AtomicInteger();

        ArrayList<String> scripts = new ArrayList<>();

        scripts.add("neo4j-scripts/001-init.cypher");
        scripts.add("neo4j-scripts/002-init.cypher");

        InitializedDbContainerConfig config = mock(InitializedDbContainerConfig.class);
        when(config.getInitScripts()).thenReturn(scripts);
        when(config.getVersion()).thenReturn("latest");

        GenericDBContainer container = new GenericDbInitializedContainer("hello-world", config) {
            @Override
            protected void customizeResource() throws IOException, InterruptedException {
            }

            @Override
            protected void exposePorts() {
            }

            @Override
            protected void runScriptContent(String scriptContent) throws IOException, InterruptedException {
                times.addAndGet(1);
            }
        };

        container.start();

        assertThat(times.get()).isEqualTo(2);
        container.stop();
    }

    @Test
    void shouldCallRunScriptForEveryScriptWithoutOverrideAndDoNothing() {
        ArrayList<String> scripts = spy(new ArrayList<>());

        scripts.add("neo4j-scripts/001-init.cypher");
        scripts.add("neo4j-scripts/002-init.cypher");

        InitializedDbContainerConfig config = mock(InitializedDbContainerConfig.class);
        when(config.getInitScripts()).thenReturn(scripts);
        when(config.getVersion()).thenReturn("latest");

        GenericDBContainer container = new GenericDbInitializedContainer("hello-world", config) {
            @Override
            protected void customizeResource() throws IOException, InterruptedException {
            }

            @Override
            protected void exposePorts() {
            }
        };

        container.start();
        verify(scripts).iterator();

        container.stop();
    }

    @Test
    void shouldThrowExceptionWhenRunScriptThrowIOException() {
        ArrayList<String> scripts = new ArrayList<>();

        scripts.add("neo4j-scripts/001-init.cypher");
        scripts.add("neo4j-scripts/002-init.cypher");
        InitializedDbContainerConfig config = mock(InitializedDbContainerConfig.class);
        when(config.getInitScripts()).thenReturn(scripts);
        when(config.getVersion()).thenReturn("latest");

        GenericDBContainer container = new GenericDbInitializedContainer("hello-world", config) {
            @Override
            protected void customizeResource() throws IOException, InterruptedException {
            }

            @Override
            protected void exposePorts() {
            }

            @Override
            protected void runScriptContent(String scriptContent) throws IOException, InterruptedException {
                throw new IOException();
            }
        };

        assertThatThrownBy(container::start)
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IOException.class);
        container.stop();

    }

    @Test
    void shouldThrowExceptionWhenRunScriptThrowInterruptException() {
        ArrayList<String> scripts = new ArrayList<>();

        scripts.add("neo4j-scripts/001-init.cypher");
        scripts.add("neo4j-scripts/002-init.cypher");
        InitializedDbContainerConfig config = mock(InitializedDbContainerConfig.class);
        when(config.getInitScripts()).thenReturn(scripts);
        when(config.getVersion()).thenReturn("latest");

        GenericDBContainer container = new GenericDbInitializedContainer("hello-world", config) {
            @Override
            protected void customizeResource() throws IOException, InterruptedException {
            }

            @Override
            protected void exposePorts() {
            }

            @Override
            protected void runScriptContent(String scriptContent) throws IOException, InterruptedException {
                throw new InterruptedException();
            }
        };

        assertThatThrownBy(container::start)
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(InterruptedException.class);
        container.stop();

    }


}
