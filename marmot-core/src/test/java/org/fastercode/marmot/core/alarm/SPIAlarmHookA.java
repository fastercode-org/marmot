package org.fastercode.marmot.core.alarm;

import lombok.extern.slf4j.Slf4j;
import org.fastercode.marmot.core.alarm.hook.AlarmHook;
import org.fastercode.marmot.core.alarm.hook.AlarmReply;

@Slf4j
public class SPIAlarmHookA implements AlarmHook {
    public SPIAlarmHookA(){
        log.info("new alarm - {}", this.getClass().getSimpleName());
    }
    @Override
    public AlarmReply alarm(AlarmItem item) {
        log.info("{}; {}", this.getClass().getSimpleName(), item);
        return AlarmReply.CONTINUE;
    }

    @Override
    public AlarmHook getType() {
        return null;
    }
}

