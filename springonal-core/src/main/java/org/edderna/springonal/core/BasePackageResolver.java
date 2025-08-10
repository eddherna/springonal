package org.edderna.springonal.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class BasePackageResolver {

    private BasePackageResolver() {
    }

    static Set<String> resolveBasePackage(Class<?> clazz) {
        return Stream.concat(
                Stream.of(clazz.getPackageName()),
                Stream.concat(
                        Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, ComponentScans.class))
                                .stream()
                                .flatMap(scans -> Arrays.stream(scans.value()))
                                .flatMap(scan -> resolvePackages(scan).stream()),
                        Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, ComponentScan.class))
                                .stream()
                                .flatMap(scan -> resolvePackages(scan).stream())
                )
        ).collect(Collectors.toSet());
    }

    private static Set<String> resolvePackages(ComponentScan componentScan) {
        return Stream.concat(
                Arrays.stream(componentScan.basePackages()),
                Arrays.stream(componentScan.basePackageClasses()).map(Class::getPackageName)
        ).collect(Collectors.toSet());
    }
}
