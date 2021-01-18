package org.fastercode.marmot.core.properties;

import org.fastercode.marmot.common.properties.TypedProperties;
import org.fastercode.marmot.common.properties.hook.TypedPropertyHook;
import org.fastercode.marmot.common.spi.NewInstanceServiceLoader;
import org.fastercode.marmot.common.spi.order.OrderedServiceLoader;
import org.fastercode.marmot.core.properties.hook.CorePropertyHook;

import java.util.Collection;
import java.util.Properties;

/**
 * Typed properties of configuration.
 */
public final class CoreProperties extends TypedProperties<CorePropertyKey> {
    private final Collection<? extends TypedPropertyHook> propertyHooks = OrderedServiceLoader.newServiceInstances(CorePropertyHook.class);

    static {
        NewInstanceServiceLoader.register(CorePropertyHook.class);
    }

    public CoreProperties(final Properties props) {
        super(CorePropertyKey.class, props);
        super.setTypedPropertyHooks((Collection<? extends TypedPropertyHook<CorePropertyKey>>) propertyHooks);
    }

    private static class Instance {
        private static final CoreProperties properties = new CoreProperties(System.getProperties());
    }

    public static <T> T get(CorePropertyKey key) {
        return Instance.properties.getValue(key);
    }
}
