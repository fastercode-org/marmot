package org.fastercode.marmot.monitor.log.properties;

import org.fastercode.marmot.common.properties.TypedProperties;
import org.fastercode.marmot.common.properties.hook.TypedPropertyHook;
import org.fastercode.marmot.common.spi.NewInstanceServiceLoader;
import org.fastercode.marmot.monitor.log.properties.hook.LogPropertyHook;

import java.util.Collection;
import java.util.Properties;

public class ErrorLogProperties extends TypedProperties<ErrorLogPropertyKey> {
    private final Collection<? extends TypedPropertyHook> propertyHooks = NewInstanceServiceLoader.newServiceInstances(LogPropertyHook.class);

    static {
        NewInstanceServiceLoader.register(LogPropertyHook.class);
    }

    public ErrorLogProperties(final Properties props) {
        super(ErrorLogPropertyKey.class, props);
        setTypedPropertyHooks((Collection<? extends TypedPropertyHook<ErrorLogPropertyKey>>) propertyHooks);
    }

    private static class Instance {
        private static final ErrorLogProperties properties = new ErrorLogProperties(System.getProperties());
    }

    public static <T> T get(ErrorLogPropertyKey key) {
        return Instance.properties.getValue(key);
    }
}
