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
