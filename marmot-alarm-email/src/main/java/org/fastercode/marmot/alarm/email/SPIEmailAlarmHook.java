package org.fastercode.marmot.alarm.email;

import lombok.extern.slf4j.Slf4j;
import org.fastercode.marmot.core.alarm.AlarmItem;
import org.fastercode.marmot.core.alarm.hook.AlarmHook;
import org.fastercode.marmot.core.alarm.hook.AlarmReply;

@Slf4j
public class SPIEmailAlarmHook implements AlarmHook {
    @Override
    public AlarmReply alarm(AlarmItem item) {
        if (log.isDebugEnabled()) {
            log.debug("hit the alarmHook: [{}]", this.getClass().getSimpleName());
        }
        return null;
    }
}
