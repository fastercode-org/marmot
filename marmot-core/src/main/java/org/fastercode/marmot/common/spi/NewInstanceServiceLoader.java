package org.fastercode.marmot.common.spi;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.*;

/**
 * SPI service loader for new instance for every call.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewInstanceServiceLoader {

    private static final Map<Class, Collection<Class<?>>> SERVICE_MAP = new HashMap<>();

    /**
     * Register SPI service into map for new instance.
     *
     * @param service service type
     * @param <T>     type of service
     */
    public static <T> void register(final Class<T> service) {
        for (T each : ServiceLoader.load(service)) {
            registerServiceClass(service, each);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void registerServiceClass(final Class<T> service, final T instance) {
        Collection<Class<?>> serviceClasses = SERVICE_MAP.get(service);
        if (null == serviceClasses) {
            serviceClasses = new LinkedHashSet<>();
        }
        serviceClasses.add(instance.getClass());
        SERVICE_MAP.put(service, serviceClasses);
    }

    /**
     * New service instances.
     *
     * @param service service class
     * @param <T>     type of service
     * @return service instances
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> newServiceInstances(final Class<T> service) {
        Collection<T> result = new LinkedList<>();
        if (null == SERVICE_MAP.get(service)) {
            return result;
        }
        for (Class<?> each : SERVICE_MAP.get(service)) {
            result.add((T) each.newInstance());
        }
        return result;
    }
}
