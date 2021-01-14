package org.fastercode.marmot.monitor.log.properties.hook;

import org.fastercode.marmot.common.properties.TypedPropertyKey;
import org.fastercode.marmot.common.properties.hook.TypedPropertyHook;

public interface LogPropertyHook<E extends Enum & TypedPropertyKey> extends TypedPropertyHook<E> {
}
