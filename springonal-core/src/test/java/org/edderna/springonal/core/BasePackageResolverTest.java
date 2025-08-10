package org.edderna.springonal.core;

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

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BasePackageResolverTest {

    @Test
    void shouldReturnOnlyOwnPackageWhenNoAnnotations() {
        Set<String> packages = BasePackageResolver.resolveBasePackage(NoAnnotations.class);

        assertThat(packages)
                .containsExactly(NoAnnotations.class.getPackageName());
    }

    @Test
    void shouldIncludeBasePackagesFromComponentScan() {
        Set<String> packages = BasePackageResolver.resolveBasePackage(SingleComponentScanBasePackages.class);

        assertThat(packages)
                .containsExactlyInAnyOrder(
                        "org.edderna.springonal.core",
                        "com.example.pkg1",
                        "com.example.pkg2"
                );
    }

    @Test
    void shouldIncludeBasePackageClassesFromComponentScan() {
        Set<String> packages = BasePackageResolver.resolveBasePackage(SingleComponentScanBasePackageClasses.class);

        assertThat(packages)
                .containsExactly(
                        "org.edderna.springonal.core"
                );
    }

    @Test
    void shouldIncludeAllFromComponentScans() {
        Set<String> packages = BasePackageResolver.resolveBasePackage(MultipleComponentScans.class);

        assertThat(packages)
                .containsExactlyInAnyOrder(
                        "org.edderna.springonal.core",
                        "com.example.scan1"
                );
    }

    @Test
    void shouldNotContainDuplicatePackages() {
        Set<String> packages = BasePackageResolver.resolveBasePackage(DuplicatePackages.class);

        assertThat(packages).hasSameSizeAs(Set.copyOf(packages)); // mismo tamaño que el conjunto → sin duplicados
    }


    @ComponentScan(basePackages = {"com.example.pkg1", "com.example.pkg2"})
    static class SingleComponentScanBasePackages {
    }

    @ComponentScan(basePackageClasses = {DummyClassA.class, DummyClassB.class})
    static class SingleComponentScanBasePackageClasses {
    }

    @ComponentScans({
            @ComponentScan(basePackages = {"com.example.scan1"}),
            @ComponentScan(basePackageClasses = {DummyClassA.class})
    })
    static class MultipleComponentScans {
    }

    @ComponentScans({
            @ComponentScan(basePackages = {"com.example.duplicate"}),
            @ComponentScan(basePackageClasses = {DuplicateDummy.class})
    })
    @ComponentScan(basePackages = {"com.example.duplicate"})
    static class DuplicatePackages {
    }

    static class DummyClassA {
    }

    static class DummyClassB {
    }

    static class DuplicateDummy {
    }

    static class NoAnnotations {
    }
}
