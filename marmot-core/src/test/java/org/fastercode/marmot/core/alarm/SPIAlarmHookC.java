package org.fastercode.marmot.core.alarm;

import lombok.extern.slf4j.Slf4j;
import org.fastercode.marmot.core.alarm.hook.AlarmHook;
import org.fastercode.marmot.core.alarm.hook.AlarmReply;

@Slf4j
public class SPIAlarmHookC implements AlarmHook {
    @Override
    public AlarmReply alarm(AlarmItem item) {
        log.info("{}; {}; {}", this.getClass().getSimpleName(), item, this);
        return AlarmReply.CONTINUE;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public AlarmHook getType() {
        return null;
    }
}

