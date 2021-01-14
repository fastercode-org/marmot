package org.fastercode.marmot.alarm.dingtalk;

import org.fastercode.marmot.alarm.dingtalk.properties.AlarmDingtalkPropertyKey;
import org.fastercode.marmot.alarm.dingtalk.properties.hook.AlarmDingtalkPropertyHook;

public class DingtalkProperty implements AlarmDingtalkPropertyHook<AlarmDingtalkPropertyKey> {
    @Override
    public <T> T getValue(AlarmDingtalkPropertyKey key, T originVal) {
        if (AlarmDingtalkPropertyKey.TEST.equals(key)) {
            return (T) Boolean.TRUE;
        }
        if (AlarmDingtalkPropertyKey.URL.equals(key)) {
            return (T) "https://oapi.dingtalk.com/robot/send?access_token=36c6a55c0a53b63bede332fbab2a5fdab407a3904524f639c16b086ea4c4c72f";
        }
        return originVal;
    }
}
