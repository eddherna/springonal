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
import org.edderna.springonal.localenv.configuration.wiremock.WiremockContainerConfig;
import org.edderna.springonal.localenv.exception.MalformedEnviromentException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WiremockContainerConfigTest {

    @Test
    void shouldCreateWiremockContainerConfigWithCompleteToml() {
        Toml toml = new Toml().read("""
                        version="2.35.0"
                        mappings="/path/to/mappings"
                        files="/path/to/files"
                        responseTemplating=true
                """);

        WiremockContainerConfig config = new WiremockContainerConfig(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "2.35.0")
                .hasFieldOrPropertyWithValue("mappings", "/path/to/mappings")
                .hasFieldOrPropertyWithValue("files", "/path/to/files")
                .hasFieldOrPropertyWithValue("responseTemplating", true);
    }

    @Test
    void shouldCreateWiremockContainerConfigWithMinimalToml() {
        Toml toml = new Toml().read("""
                        version="2.35.0"
                """);

        WiremockContainerConfig config = new WiremockContainerConfig(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "2.35.0")
                .hasFieldOrPropertyWithValue("mappings", null)
                .hasFieldOrPropertyWithValue("files", null)
                .hasFieldOrPropertyWithValue("responseTemplating", false);
    }

    @Test
    void shouldCreateWiremockContainerConfigWithResponseTemplatingFalse() {
        Toml toml = new Toml().read("""
                        version="2.35.0"
                        mappings="/mappings"
                        files="/files"
                        responseTemplating=false
                """);

        WiremockContainerConfig config = new WiremockContainerConfig(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "2.35.0")
                .hasFieldOrPropertyWithValue("mappings", "/mappings")
                .hasFieldOrPropertyWithValue("files", "/files")
                .hasFieldOrPropertyWithValue("responseTemplating", false);
    }

    @Test
    void shouldCreateWiremockContainerConfigWithOnlyMappings() {
        Toml toml = new Toml().read("""
                        version="2.35.0"
                        mappings="/only/mappings"
                """);

        WiremockContainerConfig config = new WiremockContainerConfig(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "2.35.0")
                .hasFieldOrPropertyWithValue("mappings", "/only/mappings")
                .hasFieldOrPropertyWithValue("files", null)
                .hasFieldOrPropertyWithValue("responseTemplating", false);
    }

    @Test
    void shouldCreateWiremockContainerConfigWithOnlyFiles() {
        Toml toml = new Toml().read("""
                        version="2.35.0"
                        files="/only/files"
                """);

        WiremockContainerConfig config = new WiremockContainerConfig(toml);

        assertThat(config)
                .hasFieldOrPropertyWithValue("version", "2.35.0")
                .hasFieldOrPropertyWithValue("mappings", null)
                .hasFieldOrPropertyWithValue("files", "/only/files")
                .hasFieldOrPropertyWithValue("responseTemplating", false);
    }

    @Test
    void shouldThrowExceptionWhenTomlIsEmpty() {
        Toml toml = new Toml().read("");

        MalformedEnviromentException exception = assertThrows(
                MalformedEnviromentException.class,
                () -> new WiremockContainerConfig(toml)
        );

        assertThat(exception.getMessage()).isEqualTo("Version definition cannot be null.");
    }
}