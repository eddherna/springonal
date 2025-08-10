package org.edderna.springonal.core;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

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
                .<Class<?>>map(MainClassResolver::loadClassSafely)
                .filter(MainClassResolver::isMainClass)
                .findFirst();
    }

    private static Class<?> loadClassSafely(BeanDefinition beanDefinition) {
        try {
            String beanClassName = beanDefinition.getBeanClassName();
            if (Objects.requireNonNull(beanClassName).endsWith("Kt")) {
                beanClassName = beanClassName.substring(0, beanClassName.length() - 2);
            }
            return Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to load class: " + beanDefinition.getBeanClassName(), e);
        }
    }

    private static boolean isMainClass(Class<?> candidate) {
        try {
            Method main = candidate.getDeclaredMethod("main");
            int mods = main.getModifiers();
            return Modifier.isPublic(mods) &&
                    Modifier.isStatic(mods) &&
                    main.getReturnType().equals(void.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}