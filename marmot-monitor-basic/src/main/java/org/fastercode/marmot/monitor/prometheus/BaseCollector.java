package org.fastercode.marmot.monitor.prometheus;

import io.prometheus.client.Collector;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.fastercode.marmot.core.properties.CoreProperties;
import org.fastercode.marmot.core.properties.CorePropertyKey;

/**
 * @author huyaolong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseCollector extends Collector {
    private String project = CoreProperties.get(CorePropertyKey.PROJECT);
    private String env = CoreProperties.get(CorePropertyKey.ENV);
}
