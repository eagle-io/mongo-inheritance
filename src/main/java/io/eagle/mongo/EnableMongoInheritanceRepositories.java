package io.eagle.mongo;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;
import org.springframework.data.repository.query.QueryLookupStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MongoInheritanceImporter.class)
@EnableMongoRepositories(
        repositoryBaseClass = InheritanceAwareSimpleMongoRepository.class,
        repositoryFactoryBeanClass = InheritanceAwareMongoRepositoryFactoryBean.class
)
public @interface EnableMongoInheritanceRepositories {
    @AliasFor(annotation = EnableMongoRepositories.class, attribute = "basePackages")
    String[] basePackages();

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan for annotated components. The
     * package of each class specified will be scanned. Consider creating a special no-op marker class or interface in
     * each package that serves no purpose other than being referenced by this attribute.
     */
    @AliasFor(annotation = EnableMongoRepositories.class, attribute = "basePackageClasses")
    Class<?>[] basePackageClasses() default {};

    /**
     * Specifies which types are eligible for component scanning. Further narrows the set of candidate components from
     * everything in {@link #basePackages()} to everything in the base packages that matches the given filter or filters.
     */
    @AliasFor(annotation = EnableMongoRepositories.class, attribute = "includeFilters")
    ComponentScan.Filter[] includeFilters() default {};

    /**
     * Specifies which types are not eligible for component scanning.
     */
    @AliasFor(annotation = EnableMongoRepositories.class, attribute = "excludeFilters")
    ComponentScan.Filter[] excludeFilters() default {};

    /**
     * Returns the postfix to be used when looking up custom repository implementations. Defaults to {@literal Impl}. So
     * for a repository named {@code PersonRepository} the corresponding implementation class will be looked up scanning
     * for {@code PersonRepositoryImpl}.
     *
     * @return {@literal Impl} by default.
     */
    @AliasFor(annotation = EnableMongoRepositories.class, attribute = "repositoryImplementationPostfix")
    String repositoryImplementationPostfix() default "Impl";

    /**
     * Configures the location of where to find the Spring Data named queries properties file. Will default to
     * {@code META-INFO/mongo-named-queries.properties}.
     *
     * @return empty {@link String} by default.
     */
    @AliasFor(annotation = EnableMongoRepositories.class, attribute = "namedQueriesLocation")
    String namedQueriesLocation() default "";

    /**
     * Returns the key of the {@link QueryLookupStrategy} to be used for lookup queries for query methods. Defaults to
     * {@link QueryLookupStrategy.Key#CREATE_IF_NOT_FOUND}.
     *
     * @return {@link QueryLookupStrategy.Key#CREATE_IF_NOT_FOUND} by default.
     */
    @AliasFor(annotation = EnableMongoRepositories.class, attribute = "queryLookupStrategy")
    QueryLookupStrategy.Key queryLookupStrategy() default QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;

    /**
     * Returns the {@link FactoryBean} class to be used for each repository instance. Defaults to
     * {@link MongoRepositoryFactoryBean}.
     *
     * @return {@link MongoRepositoryFactoryBean} by default.
     */
    @AliasFor(annotation = EnableMongoRepositories.class, attribute = "repositoryFactoryBeanClass")
    Class<?> repositoryFactoryBeanClass() default MongoRepositoryFactoryBean.class;

    /**
     * Configure the repository base class to be used to create repository proxies for this particular configuration.
     *
     * @return {@link DefaultRepositoryBaseClass} by default.
     * @since 1.8
     */
    @AliasFor(annotation = EnableMongoRepositories.class, attribute = "repositoryBaseClass")
    Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

    /**
     * Configures the name of the {@link MongoTemplate} bean to be used with the repositories detected.
     *
     * @return {@literal mongoTemplate} by default.
     */
    @AliasFor(annotation = EnableMongoRepositories.class, attribute = "mongoTemplateRef")
    String mongoTemplateRef() default "mongoTemplate";

    /**
     * Whether to automatically create indexes for query methods defined in the repository interface.
     *
     * @return {@literal false} by default.
     */
    @AliasFor(annotation = EnableMongoRepositories.class, attribute = "createIndexesForQueryMethods")
    boolean createIndexesForQueryMethods() default false;

    /**
     * Configures whether nested repository-interfaces (e.g. defined as inner classes) should be discovered by the
     * repositories infrastructure.
     *
     * @return {@literal false} by default.
     */
    @AliasFor(annotation = EnableMongoRepositories.class, attribute = "considerNestedRepositories")
    boolean considerNestedRepositories() default false;
}
