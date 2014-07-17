package com.godaddy.sonar.ruby.metricfu;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

public class RailBestPracticeMetric implements Metrics {

    public static final Metric RAILS_BEST_PRACTICE = new Metric.Builder("rails_best_practice", "Message", Metric.ValueType.STRING)
            .setDescription("This is a metric to store a well known message")
            .setDirection(Metric.DIRECTION_WORST)
            .setQualitative(true)
            .setDomain(CoreMetrics.DOMAIN_ISSUES)
            .create();

    public List<Metric> getMetrics() {
        return Arrays.asList(RAILS_BEST_PRACTICE);
    }
}
