package org.fastercode.marmot.monitor.prometheus;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.google.common.collect.Sets;
import io.prometheus.client.GaugeMetricFamily;
import org.fastercode.marmot.monitor.metrics.OperatingSystemGaugeSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author huyaolong
 */
public class OperatingSystemCollector extends BaseCollector {

    private static final Pattern REPLACE_CHART = Pattern.compile("[^\\w\\d]+");
    private static final HashSet<String> STRING_KEYS = Sets.newHashSet("arch", "name", "version");

    private final OperatingSystemGaugeSet gaugeSet = new OperatingSystemGaugeSet();

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();
        Map<String, Metric> metricsMap = gaugeSet.getMetrics();

        try {
            GaugeMetricFamily info = new GaugeMetricFamily("MarmotOs_info", "", labelNames("arch", "name", "version"));
            info.addMetric(labelValues(
                    ((Gauge) metricsMap.get("arch")).getValue().toString(),
                    ((Gauge) metricsMap.get("name")).getValue().toString(),
                    ((Gauge) metricsMap.get("version")).getValue().toString()
            ), 1L);
            mfs.add(info);
        } catch (Exception ignore) {
            // skip
        }

        for (Map.Entry<String, Metric> entry : metricsMap.entrySet()) {
            try {
                if (!(entry.getValue() instanceof Gauge) || STRING_KEYS.contains(entry.getKey())) {
                    continue;
                }
                Gauge v = (Gauge) entry.getValue();
                GaugeMetricFamily mf = new GaugeMetricFamily("MarmotOs_" + REPLACE_CHART.matcher(entry.getKey()).replaceAll("_"), "", labelNames("name"));
                if (!(v.getValue() instanceof Double)) {
                    mf.addMetric(labelValues(entry.getKey()), (long) v.getValue());
                } else {
                    mf.addMetric(labelValues(entry.getKey()), (double) v.getValue());
                }
                mfs.add(mf);
            } catch (Exception ignore) {
                // skip
            }
        }

        return mfs;
    }

}
