package dev.sequana.cyber;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cic.cs.unb.ca.jnetpcap.BasicFlow;

public class CostMeasurements {
    public class Measurement {
        private String name;
        private Long timestamp;
        public Long duration;
        public Long memory;

        public Measurement(String name, Long timestamp, Long duration, Long memory) {
            this.name = name;
            this.timestamp = timestamp;
            this.duration = duration;
            this.memory = memory;
        }

        public Collection<String> values() {
            String iso8601Timestamp;
            if (this.timestamp < 0) {
                iso8601Timestamp = "N/A";
            } else {
                iso8601Timestamp = Instant.ofEpochMilli(this.timestamp / 1000L).toString();
            }

            // TODO: don't rely on the order of these to match the order specified in the FlowCostRecorder.
            return Stream.of(
                iso8601Timestamp,
                name,
                duration.toString(),
                memory.toString()
            ).collect(Collectors.toList());
        }
    }

    public List<Measurement> trackedMeasurements;
    public String measuredFlowID;
    
    public CostMeasurements(BasicFlow flow) {
        this.measuredFlowID = "unknown";
        this.trackedMeasurements = new LinkedList<>();
    }

    public void addMeasurement(
            String operationName,
            Long operationTimestamp,
            Long operationDuration,
            Long usedMemory
    ) {
        this.trackedMeasurements.add(new Measurement(
            operationName,
            operationTimestamp,
            operationDuration,
            usedMemory
        ));
    }
}
