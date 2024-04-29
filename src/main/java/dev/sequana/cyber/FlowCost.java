package dev.sequana.cyber;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.FlowFeature;


public class FlowCost {
    private String flowID;
    private Map<String, List<Float>> featureOperations;

    public FlowCost(BasicFlow flow) {
        this.flowID = flow.getFlowId();

        // Initialize this map to account for the number of features it is expected to track.
        this.featureOperations = new HashMap<>(flow.featureCosts.size());
        for (FlowFeature feature : flow.featureCosts.keySet()) {
            List<Float> operationCosts = new LinkedList<>();
            CostMeasurements costMeasurements = flow.featureCosts.get(feature);
            
            // for (Float operation : ) {
                
            // }

            this.featureOperations.put(feature.getName(), operationCosts);
        }
    }

    public static Float summarize(FlowCost record, String featureName) {
        // Summation of all values associated with this flow's field.
        // Get the list of operations performed for that feature.
        // Sum up the costs accumulated over those operations.

        return (float) 0.0;
    }

    public static String get(FlowCost record, String field) {
        return "";
    }
}