package org.edderna.springonal.core;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BasePackageResolverTest {

    @Test
    @DisplayName("Debe devolver solo el paquete de la clase cuando no hay anotaciones")
    void shouldReturnOnlyOwnPackageWhenNoAnnotations() {
        Set<String> packages = BasePackageResolver.resolveBasePackage(NoAnnotations.class);

        assertThat(packages)
                .containsExactly(NoAnnotations.class.getPackageName());
    }

    @Test
    @DisplayName("Debe incluir basePackages definidos en @ComponentScan")
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
    @DisplayName("Debe incluir basePackageClasses definidos en @ComponentScan")
    void shouldIncludeBasePackageClassesFromComponentScan() {
        Set<String> packages = BasePackageResolver.resolveBasePackage(SingleComponentScanBasePackageClasses.class);

        assertThat(packages)
                .containsExactly(
                        "org.edderna.springonal.core"
                );
    }

    @Test
    @DisplayName("Debe incluir todos los paquetes de @ComponentScans")
    void shouldIncludeAllFromComponentScans() {
        Set<String> packages = BasePackageResolver.resolveBasePackage(MultipleComponentScans.class);

        assertThat(packages)
                .containsExactlyInAnyOrder(
                        "org.edderna.springonal.core",
                        "com.example.scan1"
                );
    }

    @Test
    @DisplayName("No debe contener duplicados en la lista de paquetes")
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
