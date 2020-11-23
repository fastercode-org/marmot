package org.fastercode.marmot.core.alarm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fastercode.marmot.core.alarm.hook.SPIAlarmHook;

/**
 * package-private method, for test.
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class Alarm {
    private final SPIAlarmHook alarmHook = new SPIAlarmHook();

    public void alarm(AlarmItem item) {
        if (log.isDebugEnabled()) {
            log.debug("do alarm, AlarmItem = \n{}", JSON.toJSONString(item,
                    SerializerFeature.PrettyFormat,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty
            ));
        }
        alarmHook.alarm(item);
    }

    // single ---------
    private static final class NewInstance {
        private static final Alarm instance = new Alarm();
    }

    public static Alarm getInstance() {
        return NewInstance.instance;
    }
}
