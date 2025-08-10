package org.edderna.springonal.core;

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
