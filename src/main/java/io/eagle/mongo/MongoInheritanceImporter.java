package io.eagle.mongo;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.*;

public class MongoInheritanceImporter implements ImportBeanDefinitionRegistrar {
    private static final BeanNameGenerator BEAN_NAME_GENERATOR = AnnotationBeanNameGenerator.INSTANCE;
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableMongoInheritanceRepositories.class.getCanonicalName(), false));

        Set<String> basePackages = Set.of(attributes.getStringArray("basePackages"));

        BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(MongoInheritanceScanner.class)
                .addConstructorArgValue(basePackages)
                .setScope(ConfigurableBeanFactory.SCOPE_SINGLETON)
                .getBeanDefinition();

        String beanClassName = BEAN_NAME_GENERATOR.generateBeanName(beanDefinition, registry);

        if (!registry.containsBeanDefinition(beanClassName)) {
            registry.registerBeanDefinition(beanClassName, beanDefinition);
            ((DefaultListableBeanFactory) registry).getBean(beanClassName);
        }
    }
}
