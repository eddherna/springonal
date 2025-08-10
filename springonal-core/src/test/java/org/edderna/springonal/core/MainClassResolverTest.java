package org.edderna.springonal.core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainClassResolverTest {

    static class ValidMainClass {
        public static void main() {}
    }

    static class ValidArgsMainClass {
        public static void main() {}
    }

    static class NoMainClass {}

    static class InvalidMainClass {
        // main existe pero no es public static void
        void main() {}
    }

    @Test
    void shouldFindValidMainClass() {
        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);
        BeanDefinition beanDef = mock(BeanDefinition.class);

        when(registry.getBeanDefinitionNames()).thenReturn(new String[]{"bean"});
        when(registry.getBeanDefinition("bean")).thenReturn(beanDef);
        when(beanDef.getBeanClassName()).thenReturn(ValidMainClass.class.getName());

        Optional<Class<?>> result = MainClassResolver.findMainClass(registry);

        assertTrue(result.isPresent());
        assertEquals(ValidMainClass.class, result.get());
    }

    @Test
    void shouldFindValidArgsMainClass() {
        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);
        BeanDefinition beanDef = mock(BeanDefinition.class);

        when(registry.getBeanDefinitionNames()).thenReturn(new String[]{"bean"});
        when(registry.getBeanDefinition("bean")).thenReturn(beanDef);
        when(beanDef.getBeanClassName()).thenReturn(ValidArgsMainClass.class.getName());

        Optional<Class<?>> result = MainClassResolver.findMainClass(registry);

        assertTrue(result.isPresent());
        assertEquals(ValidArgsMainClass.class, result.get());
    }

    @Test
    void shouldReturnEmptyIfNoMainClassFound() {
        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);
        BeanDefinition beanDef = mock(BeanDefinition.class);

        when(registry.getBeanDefinitionNames()).thenReturn(new String[]{"bean"});
        when(registry.getBeanDefinition("bean")).thenReturn(beanDef);
        when(beanDef.getBeanClassName()).thenReturn(NoMainClass.class.getName());

        Optional<Class<?>> result = MainClassResolver.findMainClass(registry);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenClassNotFound() {
        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);
        BeanDefinition beanDef = mock(BeanDefinition.class);

        when(registry.getBeanDefinitionNames()).thenReturn(new String[]{"bean"});
        when(registry.getBeanDefinition("bean")).thenReturn(beanDef);
        when(beanDef.getBeanClassName()).thenReturn("com.example.DoesNotExist");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> MainClassResolver.findMainClass(registry));

        assertTrue(ex.getMessage().contains("Unable to load class"));
    }

    @Test
    void shouldHandleKotlinClassNameSuffix() {
        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);
        BeanDefinition beanDef = mock(BeanDefinition.class);

        when(registry.getBeanDefinitionNames()).thenReturn(new String[]{"bean"});
        when(registry.getBeanDefinition("bean")).thenReturn(beanDef);
        when(beanDef.getBeanClassName()).thenReturn(ValidMainClass.class.getName() + "Kt");

        Optional<Class<?>> result = MainClassResolver.findMainClass(registry);

        assertTrue(result.isPresent());
        assertEquals(ValidMainClass.class, result.get());
    }

    @Test
    void shouldIgnoreInvalidMainMethodSignature() {
        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);
        BeanDefinition beanDef = mock(BeanDefinition.class);

        when(registry.getBeanDefinitionNames()).thenReturn(new String[]{"bean"});
        when(registry.getBeanDefinition("bean")).thenReturn(beanDef);
        when(beanDef.getBeanClassName()).thenReturn(InvalidMainClass.class.getName());

        Optional<Class<?>> result = MainClassResolver.findMainClass(registry);

        assertTrue(result.isEmpty());
    }
}
