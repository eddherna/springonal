package org.edderna.springonal.spring.boot.starter;

import org.edderna.springonal.core.SpringonalComponentScanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class SpringonalAnnotationAutoConfiguration {

    @Bean
    public BeanDefinitionRegistryPostProcessor postProcessor() {
        return new SpringonalComponentScanPostProcessor();
    }
}
