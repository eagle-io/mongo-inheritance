package io.eagle.mongo;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.lang.Nullable;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Calculates subclasses for any mongo entity class. We use string class names instead of classes here, due to different classloader
 * problem with spring boot libraries.
 */
public class MongoInheritanceScanner {

	protected List<String> classes = Collections.synchronizedList(new ArrayList<>());
	protected Map<String, List<String>> allClasses = new ConcurrentHashMap<>();
	protected Map<Class, List<String>> aliases = new ConcurrentHashMap<>();

	private static MongoInheritanceScanner instance;

	public MongoInheritanceScanner(String[] basePackages) {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(TypeAlias.class));
		Arrays.stream(basePackages).forEach(basePackage -> {
					provider.findCandidateComponents(basePackage).forEach(it -> {
						classes.add(it.getBeanClassName());
					});
					System.out.println("mongo-inheritance: found " + classes.size() + " models under " + basePackage);
				});

		instance = this;
	}

	public static MongoInheritanceScanner getInstance() {
		return instance;
	}

	/**
	 * All classes of clazz, together with subclasses.
	 */
	@SuppressWarnings("unchecked")
	public List<Class> getAllClasses(String className, ClassLoader classLoader) {
		if (!allClasses.containsKey(className)) {

			allClasses.computeIfAbsent(className, clazz1 -> classes.stream()
			.map(it -> {
				try {
					return Class.forName(it);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			})
			.filter(it -> {
				try {
					return Class.forName(className).isAssignableFrom(it);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			})
				.map(Class::getName)
			.collect(Collectors.toList()));

		}

		return allClasses.get(className).stream()
			.map(it -> {
				try {
					return classLoader.loadClass(it);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			})
			.collect(Collectors.toList());
	}

	/**
	 * All aliases of clazz, together with subclass aliases.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getAliases(Class clazz) {
		return aliases.computeIfAbsent(clazz, clazz1 -> getAllClasses(clazz1.getName(), clazz1.getClassLoader()).stream()
			.map(this::findAlias)
			.filter(Objects::nonNull)
			.collect(Collectors.toList()));
	}

	public String findAlias(Class<?> clazz) {
		return (!Modifier.isAbstract(clazz.getModifiers()) && clazz.isAnnotationPresent(TypeAlias.class))
			? clazz.getAnnotation(TypeAlias.class).value()
			: null;
	}

	@Nullable
	public Criteria createInheritanceCritera(List<String> aliases) {
		if (!aliases.isEmpty()) {
			return new Criteria().orOperator(aliases.stream()
				.map(alias -> where("_class").is(alias))
				.toArray(Criteria[]::new));
		}

		return null;
	}

	@Nullable
	public Criteria createInheritanceCritera(Class<?> clazz) {
		return createInheritanceCritera(getAliases(clazz));
	}

}
