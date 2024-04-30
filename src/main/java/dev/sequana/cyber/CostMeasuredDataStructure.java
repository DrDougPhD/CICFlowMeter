package dev.sequana.cyber;

// import java.util.HashMap;
// import java.util.Map;

// import cic.cs.unb.ca.jnetpcap.BasicFlow;
// import cic.cs.unb.ca.jnetpcap.FlowFeature;

public interface CostMeasuredDataStructure {
    public Long getStartTime();
    
    // private Map<FlowFeature, CostMeasurements> featureCostMeasurements;

    // public CostMeasuredDataStructure(
    //         BasicFlow flow,
    //         Map<FlowFeature, CostMeasurements> featureCosts,
    //         FlowFeature... associatedFeatures
    // ) {
    //     super();

    //     this.featureCostMeasurements = new HashMap<>(associatedFeatures.length);

    //     for (FlowFeature feature : associatedFeatures) {
    //         CostMeasurements measurements = new CostMeasurements(flow);

    //         // Locally track this cost measurement.
    //         featureCostMeasurements.put(feature, measurements);

    //         // Allow the outside world to also have a reference to this measurement.
    //         featureCosts.put(feature, measurements);
    //     }
    // }
}
