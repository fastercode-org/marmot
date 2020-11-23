package org.fastercode.marmot.core.alarm.hook;

import org.fastercode.marmot.common.spi.order.OrderAware;
import org.fastercode.marmot.core.alarm.AlarmItem;

public interface AlarmHook extends OrderAware<AlarmHook> {
    /**
     * 执行一个报警, 并返回后续报警动作
     *
     * @param item 具体的报警数据
     * @return AlarmReply 后续报警动作, 继续 or 停止
     */
    default AlarmReply alarm(AlarmItem item) {
        return AlarmReply.CONTINUE;
    }
}
