package dev.sequana.cyber;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import cic.cs.unb.ca.jnetpcap.FlowFeature;

public class CostMeasuredSummaryStatistics extends SummaryStatistics {

    private Map<FlowFeature, CostMeasurements> featureCostMeasurements;
    private long startTime = 0L;
    private long duration = 0L;

    public CostMeasuredSummaryStatistics() {}

    public CostMeasuredSummaryStatistics(Map<FlowFeature, CostMeasurements> featureCosts, FlowFeature... associatedFeatures) {
        this.featureCostMeasurements = new HashMap<>(associatedFeatures.length);

        for (FlowFeature feature : associatedFeatures) {
            CostMeasurements measurements = new CostMeasurements();

            // Locally track this cost measurement.
            featureCostMeasurements.put(feature, measurements);

            // Allow the outside world to also have a reference to this measurement.
            featureCosts.put(feature, measurements);
        }
    }

    public SummaryStatistics startMeasuring() {
        startTime = System.nanoTime();
        return this;
    }

    public void stopMeasuring() {
        duration = System.nanoTime() - startTime;
        startTime = 0L;
    }
    
    @Override
    public void addValue(double value) {
        super.addValue(value);
        stopMeasuring();
    }

    @Override
    public long getN() {
        startMeasuring();
        long captured = super.getN();
        stopMeasuring();
        return captured;
    }

    // /**
    //  * Finish taking measurements for the features associated with this measurement
    //  * and record them to a file for later analysis.
    //  * @param flowId
    //  */
    // public void finalMeasurements(String flowId) {
    //     CSVFormat
    // }
}
