package io.eagle.mongo;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.query.MongoQueryMethod;
import org.springframework.data.mongodb.repository.query.StringBasedMongoQuery;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Optional;

public class InheritanceAwareMongoRepositoryFactory extends MongoRepositoryFactory {

    private static final SpelExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    private final MongoOperations operations;

    /**
     * Creates a new {@link MongoRepositoryFactory} with the given {@link MongoOperations}.
     *
     * @param mongoOperations must not be {@literal null}.
     */
    public InheritanceAwareMongoRepositoryFactory(MongoOperations mongoOperations) {
        super(mongoOperations);
        this.operations = mongoOperations;
    }

    /**
     * Switch to our MongoQueryLookupStrategy.
     */
    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable Key key,
                                                                   QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return Optional.of(new MongoQueryLookupStrategy(operations, evaluationContextProvider,
                operations.getConverter().getMappingContext()));
    }

    /**
     * Taken from the Spring Data for MongoDB source code and modified to return InheritanceAwarePartTreeMongoQuery
     * instead of PartTreeMongoQuery. It's a static private part so copy/paste was the only way...
     */
    private static class MongoQueryLookupStrategy implements QueryLookupStrategy {

        private final MongoOperations operations;
        private final QueryMethodEvaluationContextProvider evaluationContextProvider;
        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext;

        public MongoQueryLookupStrategy(MongoOperations operations, QueryMethodEvaluationContextProvider evaluationContextProvider,
                MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext) {

            this.operations = operations;
            this.evaluationContextProvider = evaluationContextProvider;
            this.mappingContext = mappingContext;
        }

        @Override
        public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory,
                NamedQueries namedQueries) {

            MongoQueryMethod queryMethod = new MongoQueryMethod(method, metadata, factory, mappingContext);
            String namedQueryName = queryMethod.getNamedQueryName();

            if (namedQueries.hasQuery(namedQueryName)) {
                String namedQuery = namedQueries.getQuery(namedQueryName);
				return new StringBasedMongoQuery(
					enhanceQuery(namedQuery, queryMethod),
					queryMethod, operations, EXPRESSION_PARSER, evaluationContextProvider);
            } else if (queryMethod.hasAnnotatedQuery()) {
				return new StringBasedMongoQuery(
					enhanceQuery(extractAnnotatedQuery(method), queryMethod),
					queryMethod, operations, EXPRESSION_PARSER, evaluationContextProvider);
            } else {
                return new InheritanceAwarePartTreeMongoQuery(queryMethod, operations, EXPRESSION_PARSER, evaluationContextProvider);
            }
        }
    }

	/**
	 * Injects {@link TypeAlias} into mongo @{@link Query}'s {@code #{#entityName}} expression.
	 */
	private static String enhanceQuery(String query, MongoQueryMethod method) {
		String typeAlias = MongoInheritanceScanner.getInstance().findAlias(method.getEntityInformation().getJavaType());

		if (typeAlias!=null)
			return query.replaceAll("\\#\\{\\#entityName\\}", "'" + typeAlias + "'");

		return query;
	}

	protected static String extractAnnotatedQuery(Method method) {
		return Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(method, Query.class))
			.map(AnnotationUtils::getValue)
			.map(it -> (String) it)
			.filter(StringUtils::hasText)
			.orElse(null);
	}

}
