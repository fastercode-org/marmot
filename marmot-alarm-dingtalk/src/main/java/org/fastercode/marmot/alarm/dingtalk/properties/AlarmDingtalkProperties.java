package org.fastercode.marmot.alarm.dingtalk.properties;

import org.fastercode.marmot.alarm.dingtalk.properties.hook.AlarmDingtalkPropertyHook;
import org.fastercode.marmot.common.properties.TypedProperties;
import org.fastercode.marmot.common.properties.hook.TypedPropertyHook;
import org.fastercode.marmot.common.spi.NewInstanceServiceLoader;
import org.fastercode.marmot.common.spi.order.OrderedServiceLoader;

import java.util.Collection;
import java.util.Properties;

/**
 * Typed properties of configuration.
 */
public final class AlarmDingtalkProperties extends TypedProperties<AlarmDingtalkPropertyKey> {
    private final Collection<? extends TypedPropertyHook> propertyHooks = OrderedServiceLoader.newServiceInstances(AlarmDingtalkPropertyHook.class);

    static {
        NewInstanceServiceLoader.register(AlarmDingtalkPropertyHook.class);
    }

    private AlarmDingtalkProperties(final Properties props) {
        super(AlarmDingtalkPropertyKey.class, props);
        super.setTypedPropertyHooks((Collection<? extends TypedPropertyHook<AlarmDingtalkPropertyKey>>) propertyHooks);
    }

    private static class Instance {
        private static final AlarmDingtalkProperties properties = new AlarmDingtalkProperties(System.getProperties());
    }

    public static <T> T get(AlarmDingtalkPropertyKey key) {
        return Instance.properties.getValue(key);
    }
}
