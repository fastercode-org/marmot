package org.fastercode.marmot.monitor.prometheus;

import io.prometheus.client.Collector;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.fastercode.marmot.core.properties.CoreProperties;
import org.fastercode.marmot.core.properties.CorePropertyKey;
import org.fastercode.marmot.util.IpUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author huyaolong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseCollector extends Collector {
    private static final Collection<String> IP = IpUtil.getAllNoLoopbackAddresses();
    private static final int START_POS = 3;

    protected List<String> labelNames(String... strings) {
        String[] elements = new String[START_POS + strings.length];
        elements[0] = "project";
        elements[1] = "env";
        elements[2] = "ip";
        System.arraycopy(strings, 0, elements, START_POS, strings.length);
        return Arrays.asList(elements);
    }

    protected List<String> labelValues(String... strings) {
        String[] elements = new String[START_POS + strings.length];
        elements[0] = project();
        elements[1] = env();
        elements[2] = ip();
        System.arraycopy(strings, 0, elements, START_POS, strings.length);
        return Arrays.asList(elements);
    }

    private String project() {
        return CoreProperties.get(CorePropertyKey.PROJECT);
    }

    private String env() {
        return CoreProperties.get(CorePropertyKey.ENV);
    }

    private String ip() {
        if (IP == null || IP.size() == 0) {
            return "null";
        }
        if (IP.size() == 1) {
            return String.valueOf(IP.toArray()[0]);
        }
        return Arrays.toString(IP.toArray());
    }

}
