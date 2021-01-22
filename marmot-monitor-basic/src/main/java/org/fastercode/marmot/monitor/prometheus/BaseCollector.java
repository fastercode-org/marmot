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
    private static final String[] LABEL_NAME_ARRAY = new String[]{"project", "env"};

    protected List<String> labelNames(String... strings) {
        String[] elements = new String[LABEL_NAME_ARRAY.length + strings.length];
        System.arraycopy(LABEL_NAME_ARRAY, 0, elements, 0, LABEL_NAME_ARRAY.length);
        System.arraycopy(strings, 0, elements, LABEL_NAME_ARRAY.length, strings.length);
        return Arrays.asList(elements);
    }

    protected List<String> labelValues(String... strings) {
        String[] values = new String[]{project(), env()};
        String[] elements = new String[LABEL_NAME_ARRAY.length + strings.length];
        System.arraycopy(values, 0, elements, 0, values.length);
        System.arraycopy(strings, 0, elements, LABEL_NAME_ARRAY.length, strings.length);
        return Arrays.asList(elements);
    }

    private static String project() {
        return CoreProperties.get(CorePropertyKey.PROJECT);
    }

    private static String env() {
        return CoreProperties.get(CorePropertyKey.ENV);
    }

    private static String ip() {
        if (IP == null || IP.size() == 0) {
            return "null";
        }
        if (IP.size() == 1) {
            return String.valueOf(IP.toArray()[0]);
        }
        return Arrays.toString(IP.toArray());
    }

}
