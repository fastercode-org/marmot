package org.fastercode.marmot.monitor.prometheus;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import org.fastercode.marmot.monitor.metrics.RuntimeGaugeSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author huyaolong
 */
public class RuntimeCollector extends BaseCollector {

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
                if ("uptime".equals(entry.getKey())) {
                    CounterMetricFamily mf = new CounterMetricFamily("MarmotRuntime_" + REPLACE_CHART.matcher(entry.getKey()).replaceAll("_"), "", labelNames("name", "value"));
                    Object v = ((Gauge<?>) entry.getValue()).getValue();
                    mf.addMetric(labelValues(entry.getKey(), String.valueOf(v)), (long) v);
                    mfs.add(mf);
                    continue;
                }

                Gauge v = (Gauge) entry.getValue();
                GaugeMetricFamily mf = new GaugeMetricFamily("MarmotRuntime_" + REPLACE_CHART.matcher(entry.getKey()).replaceAll("_"), "", labelNames("name", "value"));

                if (v.getValue() instanceof Collection) {
                    for (Object o : (Collection<?>) v.getValue()) {
                        mf.addMetric(labelValues(entry.getKey(), String.valueOf(o)), 1);
                    }

                } else {
                    long val = 1;
                    if (v.getValue() instanceof Long) {
                        val = (long) v.getValue();
                    }
                    mf.addMetric(labelValues(entry.getKey(), String.valueOf(((Gauge<?>) entry.getValue()).getValue())), val);
                }

                mfs.add(mf);
            } catch (Exception ignore) {
                // skip
            }
        }

        return mfs;
    }

}
