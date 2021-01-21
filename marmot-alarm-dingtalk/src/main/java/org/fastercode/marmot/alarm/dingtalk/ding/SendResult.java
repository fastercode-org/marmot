package org.fastercode.marmot.alarm.dingtalk.ding;

import lombok.Data;

/**
 * @author huyaolong
 */
@Data
public class SendResult {
    private boolean success = false;
    private Integer errorCode;
    private String errorMsg;
}
