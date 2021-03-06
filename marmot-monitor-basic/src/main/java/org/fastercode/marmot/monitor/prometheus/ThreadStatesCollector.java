package org.fastercode.marmot.monitor.prometheus;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import org.fastercode.marmot.monitor.metrics.ThreadStatesGaugeSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author huyaolong
 */
public class ThreadStatesCollector extends BaseCollector {

    private static final Pattern REPLACE_CHART = Pattern.compile("[^\\w\\d]+");

    private final ThreadStatesGaugeSet gaugeSet = new ThreadStatesGaugeSet();

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();

        for (Map.Entry<String, Metric> entry : gaugeSet.getMetrics().entrySet()) {
            try {
                if (!(entry.getValue() instanceof Gauge)) {
                    continue;
                }
                if ("total_started.count".equals(entry.getKey())) {
                    CounterMetricFamily mf = new CounterMetricFamily("MarmotThreadStates_" + REPLACE_CHART.matcher(entry.getKey()).replaceAll("_"), "", labelNames("name"));
                    Object v = ((Gauge<?>) entry.getValue()).getValue();
                    mf.addMetric(labelValues(entry.getKey()), (long) v);
                    mfs.add(mf);
                    continue;
                }

                Gauge v = (Gauge) entry.getValue();
                GaugeMetricFamily mf = new GaugeMetricFamily("MarmotThreadStates_" + REPLACE_CHART.matcher(entry.getKey()).replaceAll("_"), "", labelNames("name"));

                if (v.getValue() instanceof Collection) {
                    for (Object o : (Collection<?>) v.getValue()) {
                        mf.addMetric(labelValues(entry.getKey()), 1);
                    }
                } else {
                    mf.addMetric(labelValues(entry.getKey()), (long) v.getValue());
                }

                mfs.add(mf);

            } catch (Exception ignore) {
                // skip
            }
        }

        return mfs;
    }

}
