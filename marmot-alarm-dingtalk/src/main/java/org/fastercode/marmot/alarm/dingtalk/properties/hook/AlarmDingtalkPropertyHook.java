package org.fastercode.marmot.alarm.dingtalk.properties.hook;

import org.fastercode.marmot.common.properties.TypedPropertyKey;
import org.fastercode.marmot.common.properties.hook.TypedPropertyHook;
import org.fastercode.marmot.common.spi.order.OrderAware;

public interface AlarmDingtalkPropertyHook<E extends Enum & TypedPropertyKey> extends TypedPropertyHook<E>, OrderAware {
}
