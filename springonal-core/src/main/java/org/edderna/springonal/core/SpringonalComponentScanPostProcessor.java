package org.edderna.springonal.core;

import org.edderna.springonal.annotations.application.UseCase;
import org.edderna.springonal.annotations.infrastructure.OutboundAdapter;
import org.edderna.springonal.annotations.interfaces.InboundAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SpringonalComponentScanPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private static final List<AnnotationTypeFilter> filters = List.of(
            new AnnotationTypeFilter(UseCase.class),
            new AnnotationTypeFilter(OutboundAdapter.class),
            new AnnotationTypeFilter(InboundAdapter.class)
    );

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ClassPathBeanDefinitionScanner scanner = createScanner(registry);
        resolveBasePackage(registry)
                .orElseThrow(() -> new IllegalStateException("You must specify a main class in your application to use this feature"))
                .forEach(scanner::scan);
    }

    private ClassPathBeanDefinitionScanner createScanner(BeanDefinitionRegistry registry) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
        filters.forEach(scanner::addIncludeFilter);
        return scanner;
    }

    private Optional<Set<String>> resolveBasePackage(BeanDefinitionRegistry registry) {
        return MainClassResolver.findMainClass(registry)
                .map(BasePackageResolver::resolveBasePackage);
    }
}
