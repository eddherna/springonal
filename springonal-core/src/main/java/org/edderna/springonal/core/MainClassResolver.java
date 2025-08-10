package org.edderna.springonal.core;

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