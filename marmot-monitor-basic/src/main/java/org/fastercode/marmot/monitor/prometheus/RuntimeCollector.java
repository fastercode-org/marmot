package org.fastercode.marmot.monitor.prometheus;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import org.fastercode.marmot.monitor.metrics.RuntimeGaugeSet;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author huyaolong
 */
public class RuntimeCollector extends Collector {

    private static final Pattern REPLACE_CHART = Pattern.compile("[^\\w\\d]+");

    private final RuntimeGaugeSet gaugeSet = new RuntimeGaugeSet();

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();

        for (Map.Entry<String, Metric> entry : gaugeSet.getMetrics().entrySet()) {
            try {
                if (!(entry.getValue() instanceof Gauge)) {
                    continue;
                }
                Gauge v = (Gauge) entry.getValue();
                GaugeMetricFamily mf = new GaugeMetricFamily("MarmotRuntime_" + REPLACE_CHART.matcher(entry.getKey()).replaceAll("_"), "", Arrays.asList("name", "value"));

                if (v.getValue() instanceof Collection) {
                    for (Object o : (Collection<?>) v.getValue()) {
                        mf.addMetric(Arrays.asList(entry.getKey(), String.valueOf(o)), 1);
                    }

                } else {
                    mf.addMetric(Arrays.asList(entry.getKey(), String.valueOf(((Gauge<?>) entry.getValue()).getValue())), 1);
                }

                mfs.add(mf);
            } catch (Exception ignore) {
                // skip
            }
        }

        return mfs;
    }

}
