package org.edderna.springonal.localenv.containers;

import org.edderna.springonal.localenv.configuration.AbstractContainerConfig;
import org.edderna.springonal.localenv.configuration.InitializableDatabaseContainerConfig;
import org.edderna.springonal.localenv.container.GenericDBContainer;
import org.edderna.springonal.localenv.container.InitializedDbContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class InitializedDbContainerTest {

    @Test
    void shouldCallRunScriptForEveryScript() {

        AtomicInteger times = new AtomicInteger();

        ArrayList<String> scripts = new ArrayList<>();

        scripts.add("neo4j-scripts/001-init.cypher");
        scripts.add("neo4j-scripts/002-init.cypher");

        InitializableDatabaseContainerConfig config = mock(InitializableDatabaseContainerConfig.class);
        when(config.getVersion()).thenReturn("latest");
        when(config.getInitScripts()).thenReturn(scripts);


        GenericDBContainer container = new InitializedDbContainer("hello-world", config) {
            @Override
            protected void exposePorts() {
            }

            @Override
            protected void customizeAfterStart(AbstractContainerConfig config) throws IOException, InterruptedException {

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
    void shouldNotCallRunScriptWhenScriptsAreNull() {

        AtomicInteger times = new AtomicInteger();


        InitializableDatabaseContainerConfig config = mock(InitializableDatabaseContainerConfig.class);
        when(config.getVersion()).thenReturn("latest");
        when(config.getInitScripts()).thenReturn(null);


        GenericDBContainer container = new InitializedDbContainer("hello-world", config) {
            @Override
            protected void exposePorts() {
            }

            @Override
            protected void customizeAfterStart(AbstractContainerConfig config) throws IOException, InterruptedException {

            }

            @Override
            protected void runScriptContent(String scriptContent) throws IOException, InterruptedException {
                times.addAndGet(1);
            }
        };

        container.start();

        assertThat(times.get()).isEqualTo(0);
        container.stop();
    }

    @Test
    void shouldCallRunScriptForEveryScriptWithoutOverrideAndDoNothing() {
        ArrayList<String> scripts = spy(new ArrayList<>());

        scripts.add("neo4j-scripts/001-init.cypher");
        scripts.add("neo4j-scripts/002-init.cypher");

        InitializableDatabaseContainerConfig config = mock(InitializableDatabaseContainerConfig.class);
        when(config.getVersion()).thenReturn("latest");
        when(config.getInitScripts()).thenReturn(scripts);

        GenericDBContainer container = new InitializedDbContainer("hello-world", config) {
            @Override
            protected void customizeAfterStart(AbstractContainerConfig config) throws IOException, InterruptedException {
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

        InitializableDatabaseContainerConfig config = mock(InitializableDatabaseContainerConfig.class);
        when(config.getVersion()).thenReturn("latest");
        when(config.getInitScripts()).thenReturn(scripts);

        GenericDBContainer container = new InitializedDbContainer("hello-world", config) {
            @Override
            protected void customizeAfterStart(AbstractContainerConfig config) throws IOException, InterruptedException {
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

        InitializableDatabaseContainerConfig config = mock(InitializableDatabaseContainerConfig.class);
        when(config.getVersion()).thenReturn("latest");
        when(config.getInitScripts()).thenReturn(scripts);

        GenericDBContainer container = new InitializedDbContainer("hello-world", config) {
            @Override
            protected void customizeAfterStart(AbstractContainerConfig config) throws IOException, InterruptedException {
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
