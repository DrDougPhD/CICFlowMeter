package dev.sequana.cyber;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.BasicPacketInfo;
import cic.cs.unb.ca.jnetpcap.FlowFeature;

public class CostMeasuredSummaryStatistics extends SummaryStatistics {
    private Map<FlowFeature, CostMeasurements> featureCostMeasurements;

    // Something about current context
    private List<FlowFeature> currentlyApplicableFeatures;
    private long startTime = 0L;
    private long duration = 0L;
    private long operationTimestamp;
    private boolean noActiveMeasurements = true;

    public CostMeasuredSummaryStatistics(
            BasicFlow flow,
            Map<FlowFeature, CostMeasurements> featureCosts,
            FlowFeature... associatedFeatures
    ) {
        this.featureCostMeasurements = new HashMap<>(associatedFeatures.length);

        for (FlowFeature feature : associatedFeatures) {
            CostMeasurements measurements = new CostMeasurements(flow);

            // Locally track this cost measurement.
            featureCostMeasurements.put(feature, measurements);

            // Allow the outside world to also have a reference to this measurement.
            featureCosts.put(feature, measurements);
        }

        this.currentlyApplicableFeatures = new LinkedList<>();
    }

    public CostMeasuredSummaryStatistics() {}

    public SummaryStatistics startMeasuring(BasicPacketInfo packet) {
        this.operationTimestamp = packet.getTimeStamp();
        return this.startMeasuring();
        }

    public SummaryStatistics startMeasuring() {
        this.noActiveMeasurements = false;
        startTime = System.nanoTime();
        return this;
    }

    public void stopMeasuring(String operationName, FlowFeature... onlyAppliesToFeatures) {
        duration = System.nanoTime() - startTime;

        if (this.noActiveMeasurements) {
            return;
        }

        this.noActiveMeasurements = true;

        Stream<FlowFeature> featuresToUpdate;
        if (onlyAppliesToFeatures.length == 0) {
            // Measurement applies to all features
            featuresToUpdate = this.featureCostMeasurements.keySet().stream();
        } else {
            featuresToUpdate = Arrays.asList(onlyAppliesToFeatures).stream();
        }

        featuresToUpdate.forEach(
            (feature) -> featureCostMeasurements.get(feature)
                .addMeasurement(
                    operationName,
                    this.operationTimestamp,
                    this.duration,
                    0L
                )
        );

        this.startTime = 0L;
        this.operationTimestamp = -1;
        this.currentlyApplicableFeatures.clear();
    }
    
    // public long getN() {
    //     long captured = super.getN();
    //     stopMeasuring("getN");
    //     return captured;
    // }

    // @Override
    // public long getN() {
    //     startMeasuring();
    //     long captured = super.getN();
    //     stopMeasuring("getN");
    //     return captured;
    // }

    public void addValue(BasicPacketInfo packet, String operationName, DoubleSupplier sourceOperation) {
        startMeasuring(packet);
        super.addValue(sourceOperation.getAsDouble());
        stopMeasuring(operationName);
    }

    public long getN(FlowFeature... onlyAppliesToFeatures) {
        startMeasuring();
        long n = super.getN();
        stopMeasuring("getN", onlyAppliesToFeatures);
        return n;
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
