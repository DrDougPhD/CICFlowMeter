package dev.sequana.cyber;

public class CostlyComputationalOperation {
    private String identifier;
    private String featureName;
    private String formattedTimestamp;
    private String operationName;
    private Long operationDuration;
    private Long usedMemory;

    public CostlyComputationalOperation(String identifier, String featureName, String formattedTimestamp, String operationName, Long operationDuration, Long usedMemory) {
        this.identifier = identifier;
        this.featureName = featureName;
        this.formattedTimestamp = formattedTimestamp;
        this.operationName = operationName;
        this.operationDuration = operationDuration;
        this.usedMemory = usedMemory;
    }
}
