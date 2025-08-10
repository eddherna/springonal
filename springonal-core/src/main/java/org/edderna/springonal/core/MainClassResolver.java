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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

final class MainClassResolver {

    private MainClassResolver() {
    }

    static Optional<Class<?>> findMainClass(BeanDefinitionRegistry registry) {
        return Arrays.stream(registry.getBeanDefinitionNames())
                .map(registry::getBeanDefinition)
                .map(BeanDefinition::getBeanClassName)
                .filter(Objects::nonNull)
                .<Class<?>>map(MainClassResolver::loadClassSafely)
                .filter(Objects::nonNull)
                .filter(MainClassResolver::isMainClass)
                .findFirst();
    }

    private static Class<?> loadClassSafely(@NonNull String beanName) {
        try {
            if (beanName.endsWith("Kt")) {
                beanName = beanName.substring(0, beanName.length() - 2);
            }
            return Class.forName(beanName);

        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static boolean isMainClass(Class<?> candidate) {
        try {
            Method main = candidate.getDeclaredMethod("main", String[].class);
            int mods = main.getModifiers();
            return Modifier.isPublic(mods) &&
                    Modifier.isStatic(mods) &&
                    main.getReturnType().equals(void.class);
        } catch (NoSuchMethodException _) {
            return false;
        }
    }
}
