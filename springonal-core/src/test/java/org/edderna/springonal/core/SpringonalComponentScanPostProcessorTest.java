package org.edderna.springonal.core;

import org.edderna.springonal.annotations.application.UseCase;
import org.edderna.springonal.annotations.infrastructure.OutboundAdapter;
import org.edderna.springonal.annotations.interfaces.InboundAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpringonalComponentScanPostProcessorTest {

    private SpringonalComponentScanPostProcessor processor;
    private BeanDefinitionRegistry registry;

    @BeforeEach
    void setUp() {
        processor = new SpringonalComponentScanPostProcessor();
        registry = mock(BeanDefinitionRegistry.class);
    }

    @Test
    void shouldScanAndRegisterSpringonalComponents() {
        // Given
        String[] beanNames = {"testMainClass"};
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        
        when(registry.getBeanDefinitionNames()).thenReturn(beanNames);
        when(registry.getBeanDefinition("testMainClass")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn(TestMainClass.class.getName());

        try (MockedConstruction<ClassPathBeanDefinitionScanner> scannerMock = mockConstruction(
                ClassPathBeanDefinitionScanner.class,
                (mock, context) -> {
                    when(mock.scan(any(String.class))).thenReturn(3);
                })) {

            // When
            processor.postProcessBeanDefinitionRegistry(registry);

            // Then
            assertThat(scannerMock.constructed()).hasSize(1);
            ClassPathBeanDefinitionScanner scanner = scannerMock.constructed().get(0);
            
            // Verify that filters were added for all Springonal annotations
            verify(scanner, times(3)).addIncludeFilter(any(AnnotationTypeFilter.class));
            
            // Verify that scan was called with the base package
            verify(scanner).scan("org.edderna.springonal.core");
        }
    }

    @Test
    void shouldAddCorrectAnnotationFilters() {
        // Given
        String[] beanNames = {"testMainClass"};
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        
        when(registry.getBeanDefinitionNames()).thenReturn(beanNames);
        when(registry.getBeanDefinition("testMainClass")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn(TestMainClass.class.getName());

        ArgumentCaptor<AnnotationTypeFilter> filterCaptor = ArgumentCaptor.forClass(AnnotationTypeFilter.class);

        try (MockedConstruction<ClassPathBeanDefinitionScanner> scannerMock = mockConstruction(
                ClassPathBeanDefinitionScanner.class)) {

            // When
            processor.postProcessBeanDefinitionRegistry(registry);

            // Then
            ClassPathBeanDefinitionScanner scanner = scannerMock.constructed().get(0);
            verify(scanner, times(3)).addIncludeFilter(filterCaptor.capture());
            
            // Verify that the correct annotation filters were added
            assertThat(filterCaptor.getAllValues())
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
        when(registry.getBeanDefinitionNames()).thenReturn(new String[]{});

        // When & Then
        assertThatThrownBy(() -> processor.postProcessBeanDefinitionRegistry(registry))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("You must specify a main class in your application to use this feature");
    }

    @Test
    void shouldThrowExceptionWhenMainClassCannotBeLoaded() {
        // Given
        String[] beanNames = {"invalidClass"};
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        
        when(registry.getBeanDefinitionNames()).thenReturn(beanNames);
        when(registry.getBeanDefinition("invalidClass")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn("com.invalid.NonExistentClass");

        // When & Then
        assertThatThrownBy(() -> processor.postProcessBeanDefinitionRegistry(registry))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("You must specify a main class in your application to use this feature");
    }

    @Test
    void shouldHandleMultipleBasePackages() {
        // Given
        String[] beanNames = {"testMainClassWithComponentScan"};
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        
        when(registry.getBeanDefinitionNames()).thenReturn(beanNames);
        when(registry.getBeanDefinition("testMainClassWithComponentScan")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn(TestMainClassWithComponentScan.class.getName());

        ArgumentCaptor<String> packageCaptor = ArgumentCaptor.forClass(String.class);

        try (MockedConstruction<ClassPathBeanDefinitionScanner> scannerMock = mockConstruction(
                ClassPathBeanDefinitionScanner.class)) {

            // When
            processor.postProcessBeanDefinitionRegistry(registry);

            // Then
            ClassPathBeanDefinitionScanner scanner = scannerMock.constructed().get(0);
            verify(scanner, times(2)).scan(packageCaptor.capture());
            
            assertThat(packageCaptor.getAllValues())
                    .containsExactlyInAnyOrder(
                            "org.edderna.springonal.core",
                            "com.example.additional"
                    );
        }
    }

    @Test
    void shouldSkipNullBeanClassNames() {
        // Given
        String[] beanNames = {"validClass", "nullClass"};
        BeanDefinition validBeanDefinition = mock(BeanDefinition.class);
        BeanDefinition nullBeanDefinition = mock(BeanDefinition.class);
        
        when(registry.getBeanDefinitionNames()).thenReturn(beanNames);
        when(registry.getBeanDefinition("validClass")).thenReturn(validBeanDefinition);
        when(registry.getBeanDefinition("nullClass")).thenReturn(nullBeanDefinition);
        when(validBeanDefinition.getBeanClassName()).thenReturn(TestMainClass.class.getName());
        when(nullBeanDefinition.getBeanClassName()).thenReturn(null);

        try (MockedConstruction<ClassPathBeanDefinitionScanner> scannerMock = mockConstruction(
                ClassPathBeanDefinitionScanner.class)) {

            // When
            processor.postProcessBeanDefinitionRegistry(registry);

            // Then
            assertThat(scannerMock.constructed()).hasSize(1);
            ClassPathBeanDefinitionScanner scanner = scannerMock.constructed().get(0);
            verify(scanner).scan("org.edderna.springonal.core");
        }
    }

    @Test
    void shouldHandleBeansExceptionGracefully() {
        // Given
        when(registry.getBeanDefinitionNames()).thenThrow(new RuntimeException("Registry error"));

        // When & Then
        assertThatThrownBy(() -> processor.postProcessBeanDefinitionRegistry(registry))
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