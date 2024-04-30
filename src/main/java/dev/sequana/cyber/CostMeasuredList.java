package dev.sequana.cyber;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Stream;

import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.BasicPacketInfo;
import cic.cs.unb.ca.jnetpcap.FlowFeature;

public class CostMeasuredList extends LinkedList<BasicPacketInfo> implements CostMeasuredDataStructure {
    private Map<FlowFeature, CostMeasurements> featureCostMeasurements;
    private long operationTimestamp;
    private boolean noActiveMeasurements = true;
    private long startTime = 0L;
    private long duration = 0L;
    public long previousStartTime;

    public CostMeasuredList(
            BasicFlow flow,
            Map<FlowFeature, CostMeasurements> featureCosts,
            FlowFeature... associatedFeatures
    ) {
        super();

        this.featureCostMeasurements = new HashMap<>(associatedFeatures.length);

        for (FlowFeature feature : associatedFeatures) {
            CostMeasurements measurements = new CostMeasurements(flow);

            // Locally track this cost measurement.
            featureCostMeasurements.put(feature, measurements);

            // Allow the outside world to also have a reference to this measurement.
            featureCosts.put(feature, measurements);
        }
    }

    public void startMeasuring(BasicPacketInfo packet) {
        this.operationTimestamp = packet.getTimeStamp();
        this.startMeasuring();
    }

    public void startMeasuring() {
        this.noActiveMeasurements = false;
        startTime = System.nanoTime();
    }

    @Override
    public boolean add(BasicPacketInfo packet) {
        startMeasuring(packet);
        boolean result = super.add(packet);
        stopMeasuring("listAppend");
        return result;
    }

    public void stopMeasuring(String operationName) {
        duration = System.nanoTime() - startTime;
        this.previousStartTime = startTime;

        if (this.noActiveMeasurements) {
            return;
        }

        this.noActiveMeasurements = true;

        Stream<FlowFeature> featuresToUpdate = this.featureCostMeasurements.keySet().stream();
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
    }

	public void stopAndOverrideStartTime(CostMeasuredDataStructure structure, String operation) {
		this.startTime = structure.getStartTime();
		stopMeasuring(operation);
	}

    @Override
    public Long getStartTime() {
        return this.startTime;
    }
}
