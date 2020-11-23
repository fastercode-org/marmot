package org.fastercode.marmot.core.alarm.hook;

import org.fastercode.marmot.common.spi.NewInstanceServiceLoader;
import org.fastercode.marmot.common.spi.order.OrderedServiceLoader;
import org.fastercode.marmot.core.alarm.AlarmItem;

import java.util.Collection;

public class SPIAlarmHook {
    private final Collection<AlarmHook> alarmHooks = OrderedServiceLoader.newServiceInstances(AlarmHook.class);

    static {
        NewInstanceServiceLoader.register(AlarmHook.class);
    }

    public void alarm(AlarmItem item) {
        if (item == null || alarmHooks == null || alarmHooks.size() == 0) {
            return;
        }

        for (AlarmHook each : alarmHooks) {
            if (AlarmReply.ABORT.equals(each.alarm(item))) {
                break;
            }
        }
    }

}
