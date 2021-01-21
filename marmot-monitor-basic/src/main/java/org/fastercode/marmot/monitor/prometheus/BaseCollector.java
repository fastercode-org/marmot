package org.fastercode.marmot.monitor.prometheus;

import io.prometheus.client.Collector;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.fastercode.marmot.core.properties.CoreProperties;
import org.fastercode.marmot.core.properties.CorePropertyKey;

import java.util.Arrays;
import java.util.List;

/**
 * @author huyaolong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseCollector extends Collector {
    protected List<String> labelNames(String... strings) {
        String[] elements = new String[2 + strings.length];
        elements[0] = "project";
        elements[1] = "env";
        System.arraycopy(strings, 0, elements, 2, strings.length);
        return Arrays.asList(elements);
    }

    protected List<String> labelValues(String... strings) {
        String[] elements = new String[2 + strings.length];
        elements[0] = project();
        elements[1] = env();
        System.arraycopy(strings, 0, elements, 2, strings.length);
        return Arrays.asList(elements);
    }

    private String project() {
        return CoreProperties.get(CorePropertyKey.PROJECT);
    }

    private String env() {
        return CoreProperties.get(CorePropertyKey.ENV);
    }

}
