package org.fastercode.marmot.core.alarm;

import com.google.common.collect.Maps;
import lombok.Data;
import org.fastercode.marmot.util.IpUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author huyaolong
 */
@Data
public class AlarmItem implements Serializable {
    private static final long serialVersionUID = -5781685504015861729L;

    private AlarmType alarmType;

    private String tag;

    private String title;

    private String project;

    private String env = "dev";

    private Map<String, String> mdcMap;

    private Map<AlarmType.ContentKey, String> content = Maps.newLinkedHashMap();

    private String threadName = Thread.currentThread().getName();

    private String hostName = IpUtil.getHostName();

    private Collection<String> ips = IpUtil.getAllNoLoopbackAddresses();

    private Date date = new Date();

    private int hitCount = 1;
    private int perMinute = 1;

    private int hit = 1;
    private int minute = 1;
}
