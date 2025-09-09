package org.edderna.springonal.spring.boot.starter;

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

import org.assertj.core.api.Assertions;
import org.edderna.springonal.annotations.application.UseCase;
import org.edderna.springonal.annotations.infrastructure.OutboundAdapter;
import org.edderna.springonal.annotations.interfaces.InboundAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.type.filter.AnnotationTypeFilter;

class SpringonalComponentScanPostProcessorTest {

    private SpringonalComponentScanPostProcessor processor;
    private BeanDefinitionRegistry registry;

    @BeforeEach
    void setUp() {
        processor = new SpringonalComponentScanPostProcessor();
        registry = Mockito.mock(BeanDefinitionRegistry.class);
    }

    @Test
    void shouldScanAndRegisterSpringonalComponents() {
        // Given
        String[] beanNames = {"testMainClass"};
        BeanDefinition beanDefinition = Mockito.mock(BeanDefinition.class);
        
        Mockito.when(registry.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when(registry.getBeanDefinition("testMainClass")).thenReturn(beanDefinition);
        Mockito.when(beanDefinition.getBeanClassName()).thenReturn(TestMainClass.class.getName());

        try (MockedConstruction<ClassPathBeanDefinitionScanner> scannerMock = Mockito.mockConstruction(
                ClassPathBeanDefinitionScanner.class,
                (mock, context) -> {
                    Mockito.when(mock.scan(ArgumentMatchers.any(String.class))).thenReturn(3);
                })) {

            // When
            processor.postProcessBeanDefinitionRegistry(registry);

            // Then
            Assertions.assertThat(scannerMock.constructed()).hasSize(1);
            ClassPathBeanDefinitionScanner scanner = scannerMock.constructed().get(0);
            
            // Verify that filters were added for all Springonal annotations
            Mockito.verify(scanner, Mockito.times(3)).addIncludeFilter(ArgumentMatchers.any(AnnotationTypeFilter.class));
            
            // Verify that scan was called with the base package
            Mockito.verify(scanner).scan("org.edderna.springonal.spring.boot.starter");
        }
    }

    @Test
    void shouldAddCorrectAnnotationFilters() {
        // Given
        String[] beanNames = {"testMainClass"};
        BeanDefinition beanDefinition = Mockito.mock(BeanDefinition.class);
        
        Mockito.when(registry.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when(registry.getBeanDefinition("testMainClass")).thenReturn(beanDefinition);
        Mockito.when(beanDefinition.getBeanClassName()).thenReturn(TestMainClass.class.getName());

        ArgumentCaptor<AnnotationTypeFilter> filterCaptor = ArgumentCaptor.forClass(AnnotationTypeFilter.class);

        try (MockedConstruction<ClassPathBeanDefinitionScanner> scannerMock = Mockito.mockConstruction(
                ClassPathBeanDefinitionScanner.class)) {

            // When
            processor.postProcessBeanDefinitionRegistry(registry);

            // Then
            ClassPathBeanDefinitionScanner scanner = scannerMock.constructed().get(0);
            Mockito.verify(scanner, Mockito.times(3)).addIncludeFilter(filterCaptor.capture());
            
            // Verify that the correct annotation filters were added
            Assertions.assertThat(filterCaptor.getAllValues())
                    .extracting("annotationType")
                    .containsExactlyInAnyOrder(
                            UseCase.class,
                            OutboundAdapter.class,
                            InboundAdapter.class
                    );
        }
    }

    @Test
    void shouldThrowExceptionWhenNoMainClassFound() {
        // Given
        Mockito.when(registry.getBeanDefinitionNames()).thenReturn(new String[]{});

        // When & Then
        Assertions.assertThatThrownBy(() -> processor.postProcessBeanDefinitionRegistry(registry))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("You must specify a main class in your application to use this feature");
    }

    @Test
    void shouldThrowExceptionWhenMainClassCannotBeLoaded() {
        // Given
        String[] beanNames = {"invalidClass"};
        BeanDefinition beanDefinition = Mockito.mock(BeanDefinition.class);
        
        Mockito.when(registry.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when(registry.getBeanDefinition("invalidClass")).thenReturn(beanDefinition);
        Mockito.when(beanDefinition.getBeanClassName()).thenReturn("com.invalid.NonExistentClass");

        // When & Then
        Assertions.assertThatThrownBy(() -> processor.postProcessBeanDefinitionRegistry(registry))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("You must specify a main class in your application to use this feature");
    }

    @Test
    void shouldHandleMultipleBasePackages() {
        // Given
        String[] beanNames = {"testMainClassWithComponentScan"};
        BeanDefinition beanDefinition = Mockito.mock(BeanDefinition.class);
        
        Mockito.when(registry.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when(registry.getBeanDefinition("testMainClassWithComponentScan")).thenReturn(beanDefinition);
        Mockito.when(beanDefinition.getBeanClassName()).thenReturn(TestMainClassWithComponentScan.class.getName());

        ArgumentCaptor<String> packageCaptor = ArgumentCaptor.forClass(String.class);

        try (MockedConstruction<ClassPathBeanDefinitionScanner> scannerMock = Mockito.mockConstruction(
                ClassPathBeanDefinitionScanner.class)) {

            // When
            processor.postProcessBeanDefinitionRegistry(registry);

            // Then
            ClassPathBeanDefinitionScanner scanner = scannerMock.constructed().get(0);
            Mockito.verify(scanner, Mockito.times(2)).scan(packageCaptor.capture());
            
            Assertions.assertThat(packageCaptor.getAllValues())
                    .containsExactlyInAnyOrder(
                            "org.edderna.springonal.spring.boot.starter",
                            "com.example.additional"
                    );
        }
    }

    @Test
    void shouldSkipNullBeanClassNames() {
        // Given
        String[] beanNames = {"validClass", "nullClass"};
        BeanDefinition validBeanDefinition = Mockito.mock(BeanDefinition.class);
        BeanDefinition nullBeanDefinition = Mockito.mock(BeanDefinition.class);
        
        Mockito.when(registry.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when(registry.getBeanDefinition("validClass")).thenReturn(validBeanDefinition);
        Mockito.when(registry.getBeanDefinition("nullClass")).thenReturn(nullBeanDefinition);
        Mockito.when(validBeanDefinition.getBeanClassName()).thenReturn(TestMainClass.class.getName());
        Mockito.when(nullBeanDefinition.getBeanClassName()).thenReturn(null);

        try (MockedConstruction<ClassPathBeanDefinitionScanner> scannerMock = Mockito.mockConstruction(
                ClassPathBeanDefinitionScanner.class)) {

            // When
            processor.postProcessBeanDefinitionRegistry(registry);

            // Then
            Assertions.assertThat(scannerMock.constructed()).hasSize(1);
            ClassPathBeanDefinitionScanner scanner = scannerMock.constructed().get(0);
            Mockito.verify(scanner).scan("org.edderna.springonal.spring.boot.starter");
        }
    }

    @Test
    void shouldHandleBeansExceptionGracefully() {
        // Given
        Mockito.when(registry.getBeanDefinitionNames()).thenThrow(new RuntimeException("Registry error"));

        // When & Then
        Assertions.assertThatThrownBy(() -> processor.postProcessBeanDefinitionRegistry(registry))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Registry error");
    }

    // Test helper classes
    static class TestMainClass {
        public static void main(String[] args) {
            // Test main method
        }
    }

    @ComponentScan(basePackages = {"com.example.additional"})
    static class TestMainClassWithComponentScan {
        public static void main(String[] args) {
            // Test main method with component scan
        }
    }

    static class TestClassWithoutMainMethod {
        // No main method
    }

    @UseCase
    static class TestUseCase {
        // Test use case
    }

    @OutboundAdapter
    static class TestOutboundAdapter {
        // Test outbound adapter
    }

    @InboundAdapter
    static class TestInboundAdapter {
        // Test inbound adapter
    }
}
