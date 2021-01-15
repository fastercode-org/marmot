package org.fastercode.marmot.monitor.prometheus;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import org.fastercode.marmot.monitor.metrics.MemoryGaugeSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author huyaolong
 */
public class MemoryCollector extends Collector {

    private static final Pattern REPLACE_CHART = Pattern.compile("[^\\w\\d]+");

    private final MemoryGaugeSet gaugeSet = new MemoryGaugeSet();

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();

        for (Map.Entry<String, Metric> entry : gaugeSet.getMetrics().entrySet()) {
            try {
                if (!(entry.getValue() instanceof Gauge)) {
                    continue;
                }
                Gauge v = (Gauge) entry.getValue();
                GaugeMetricFamily mf = new GaugeMetricFamily("MarmotMem_" + REPLACE_CHART.matcher(entry.getKey()).replaceAll("_"), "", Arrays.asList("name", "value"));
                mf.addMetric(Arrays.asList(entry.getKey(), String.valueOf(v.getValue())), (long) v.getValue());
                mfs.add(mf);
            } catch (Exception ignore) {
                // skip
            }
        }

        return mfs;
    }

}
