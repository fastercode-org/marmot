package org.fastercode.marmot.monitor.prometheus;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.google.common.collect.Sets;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import org.fastercode.marmot.monitor.metrics.OperatingSystemGaugeSet;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author huyaolong
 */
public class OperatingSystemCollector extends Collector {

    private static final Pattern REPLACE_CHART = Pattern.compile("[^\\w\\d]+");
    private static final HashSet<String> STRING_KEYS = Sets.newHashSet("os.arch", "os.name", "os.version");

    private final OperatingSystemGaugeSet gaugeSet = new OperatingSystemGaugeSet();

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();

        GaugeMetricFamily info = new GaugeMetricFamily("MarmotOs_info", "", Arrays.asList("arch", "name", "version"));
        info.addMetric(Arrays.asList(
                ((Gauge) gaugeSet.getMetrics().get("os.arch")).getValue().toString(),
                ((Gauge) gaugeSet.getMetrics().get("os.name")).getValue().toString(),
                ((Gauge) gaugeSet.getMetrics().get("os.version")).getValue().toString()
        ), 1L);
        mfs.add(info);

        for (Map.Entry<String, Metric> entry : gaugeSet.getMetrics().entrySet()) {
            try {
                if (!(entry.getValue() instanceof Gauge) || STRING_KEYS.contains(entry.getKey())) {
                    continue;
                }
                Gauge v = (Gauge) entry.getValue();
                GaugeMetricFamily mf = new GaugeMetricFamily("MarmotOs_" + REPLACE_CHART.matcher(entry.getKey()).replaceAll("_"), "", Arrays.asList("name"));
                if (!(v.getValue() instanceof Double)) {
                    mf.addMetric(Arrays.asList(entry.getKey()), (long) v.getValue());
                } else {
                    mf.addMetric(Arrays.asList(entry.getKey()), (double) v.getValue());
                }
                mfs.add(mf);
            } catch (Exception ignore) {
                // skip
            }
        }

        return mfs;
    }

}
