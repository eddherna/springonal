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

import org.edderna.springonal.localenv.configuration.AbstractContainerConfig;
import org.edderna.springonal.localenv.container.GenericDBContainer;
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

        AbstractContainerConfig conf = mock(AbstractContainerConfig.class);
        when(conf.getVersion()).thenReturn("latest");


        GenericDBContainer container = new GenericDBContainer("hello-world", conf) {
            @Override
            protected void customizeAfterStart(AbstractContainerConfig conf) throws IOException, InterruptedException {
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
        AbstractContainerConfig conf = mock(AbstractContainerConfig.class);
        when(conf.getVersion()).thenReturn("latest");
        GenericDBContainer container = new GenericDBContainer("hello-world", conf) {
            @Override
            protected void customizeAfterStart(AbstractContainerConfig conf) throws IOException, InterruptedException {
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


}
