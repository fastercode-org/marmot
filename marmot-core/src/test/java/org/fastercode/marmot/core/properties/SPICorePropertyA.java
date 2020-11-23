package org.fastercode.marmot.core.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.fastercode.marmot.core.properties.hook.CorePropertyHook;

@Slf4j
public class SPICorePropertyA implements CorePropertyHook<CorePropertyKey> {
    public SPICorePropertyA() {
        log.info("new property - {}", this);
    }

    @Getter
    @Setter
    private int order = 100;

    @Override
    public <T> T getValue(CorePropertyKey key, T originVal) {
        log.info("get property {}", this);
        if (key.equals(CorePropertyKey.ALARM_CAPACITY_MIN)) {
            return (T) Integer.valueOf(666);
        }
        return null;
    }
}
