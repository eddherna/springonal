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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainClassResolverTest {

    static class ValidArgsMainClass {
        public static void main(String[] args) {}
    }

    static class NoMainClass {}

    static class NotPublicMainMethodClass {
        // main existe pero no es public static void
        static void main(String[] args) {}
    }

    static class NotStaticMainMethodClass {
        public void main(String[] args) {}
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

        assertThat(MainClassResolver.findMainClass(registry)).isEqualTo(Optional.empty());
    }

    @Test
    void shouldHandleKotlinClassNameSuffix() {
        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);
        BeanDefinition beanDef = mock(BeanDefinition.class);

        when(registry.getBeanDefinitionNames()).thenReturn(new String[]{"bean"});
        when(registry.getBeanDefinition("bean")).thenReturn(beanDef);
        when(beanDef.getBeanClassName()).thenReturn(ValidArgsMainClass.class.getName() + "Kt");

        Optional<Class<?>> result = MainClassResolver.findMainClass(registry);

        assertTrue(result.isPresent());
        assertEquals(ValidArgsMainClass.class, result.get());
    }

    @Test
    void shouldIgnoreNotPublicMainMethodClass() {
        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);
        BeanDefinition beanDef = mock(BeanDefinition.class);

        when(registry.getBeanDefinitionNames()).thenReturn(new String[]{"bean"});
        when(registry.getBeanDefinition("bean")).thenReturn(beanDef);
        when(beanDef.getBeanClassName()).thenReturn(NotPublicMainMethodClass.class.getName());

        Optional<Class<?>> result = MainClassResolver.findMainClass(registry);

        assertTrue(result.isEmpty());
    }


    @Test
    void shouldIgnoreNotStaticMainMethodClass() {
        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);
        BeanDefinition beanDef = mock(BeanDefinition.class);

        when(registry.getBeanDefinitionNames()).thenReturn(new String[]{"bean"});
        when(registry.getBeanDefinition("bean")).thenReturn(beanDef);
        when(beanDef.getBeanClassName()).thenReturn(NotStaticMainMethodClass.class.getName());

        Optional<Class<?>> result = MainClassResolver.findMainClass(registry);

        assertTrue(result.isEmpty());
    }
}
