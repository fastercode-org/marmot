package org.fastercode.marmot.alarm.dingtalk.ding.message;

/**
 * @author harold
 */
public interface Message {

    /**
     * 返回消息的Json格式字符串
     *
     * @return 消息的Json格式字符串
     */
    String toJsonString();
}
