package dev.sequana.cyber;

import cic.cs.unb.ca.jnetpcap.BasicFlow;


public class FlowCost {
    public FlowCost(BasicFlow flow) {
        
    }

    public static Float summarize(FlowCost record, String field) {
        // Summation of all values associated with this flow's field.
        return (float) 0.0;
    }

    public static String get(FlowCost record, String field) {
        return "";
    }
}