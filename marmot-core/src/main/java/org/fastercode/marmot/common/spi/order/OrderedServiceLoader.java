package org.fastercode.marmot.common.spi.order;

import org.fastercode.marmot.common.spi.NewInstanceServiceLoader;

import java.util.*;

/**
 * Ordered registry.
 *
 * @author huyaolong
 */
public final class OrderedServiceLoader {

    public static <T extends OrderAware> Collection<T> newServiceInstances(final Class<T> orderAwareClass) {
        PriorityQueue<T> queue = new PriorityQueue<>(Comparator.comparingInt(OrderAware::getOrder));
        for (T each : NewInstanceServiceLoader.newServiceInstances(orderAwareClass)) {
            queue.offer(each);
        }
        return (Collection<T>) Arrays.asList(queue.toArray(new OrderAware[0]));
    }

    /**
     * Get registered classes.
     *
     * @param orderAwareClass class of order aware
     * @param <T>             type of order aware class
     * @return registered classes
     */
    @SuppressWarnings("unchecked")
    public static <T extends OrderAware> Collection<Class<T>> getRegisteredClasses(final Class<T> orderAwareClass) {
        Map<Integer, Class<T>> result = new TreeMap<>();
        for (T each : NewInstanceServiceLoader.newServiceInstances(orderAwareClass)) {
            result.put(each.getOrder(), (Class<T>) each.getClass());
        }
        return result.values();
    }
}
