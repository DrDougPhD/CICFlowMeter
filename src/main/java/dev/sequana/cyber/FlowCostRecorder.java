package dev.sequana.cyber;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.FlowFeature;

public class FlowCostRecorder {
    final public static String FLOW_FEATURE_FIELD = "ComputedFeature";
    final public static String TIMESTAMP_FIELD = FlowFeature.tstp.getName();
    final public static String FLOW_ID_FIELD = FlowFeature.fid.getName();
    final public static String OPERATION_FIELD = "Operation";
    final public static String DURATION_NS_FIELD = "Duration (ns)";
    final public static String MEMORY_USED_FIELD = "Memory Used (bytes)";

    private static final Logger logger = LoggerFactory.getLogger(FlowCostRecorder.class);

    private List<Map<FlowFeature, CostMeasurements>> measurements;
    private List<String> flowIDs;

    private CSVFormat aggregatedCostsCSV;
    private Path aggregatedCostsFilePath;

    private CSVFormat operationCostsCSV;
    private Path operationCostsFilePath;

    public FlowCostRecorder(String outputDirectory) {
        this.measurements = new LinkedList<>();
        this.flowIDs = new LinkedList<>();

        String[] featureNames = Stream.of(
            FlowFeature.values()
        ).map(FlowFeature::getName)
        .collect(Collectors.toList())
        .toArray(new String[]{""});

        this.aggregatedCostsCSV = CSVFormat.DEFAULT.builder()
            .setHeader(featureNames)
            .build();
        this.aggregatedCostsFilePath = Paths.get(
            outputDirectory,
            "aggregated.costs.csv"
        );

        this.operationCostsCSV = CSVFormat.DEFAULT.builder()
            .setHeader(
                FLOW_FEATURE_FIELD,
                TIMESTAMP_FIELD,
                FLOW_ID_FIELD,
                OPERATION_FIELD,
                DURATION_NS_FIELD,
                MEMORY_USED_FIELD
            ).build();
        this.operationCostsFilePath = Paths.get(
            outputDirectory,
            "operation.costs.csv"
        );
    }

    public void finalizeFlowCosts(BasicFlow flow) {
        flow.featureCosts.values()
            .stream()
            .forEach(
                measurements -> {
                    measurements.measuredFlowID = flow.getFlowId();
                }
            );
        this.measurements.add(new HashMap<>(flow.featureCosts));
        this.flowIDs.add(flow.getFlowId());

        flow.featureCosts.clear();
    }

    public void close() {
        // TODO: maybe a set of flow IDs to verify not duplicates?
        try (
            BufferedWriter writer = Files.newBufferedWriter(operationCostsFilePath);
            CSVPrinter csvPrinter = new CSVPrinter(writer, operationCostsCSV);
        ) {
            for (Map<FlowFeature, CostMeasurements> flowMeasurements : this.measurements) {
                for (Map.Entry<FlowFeature, CostMeasurements> entry : flowMeasurements.entrySet()) {
                    
                    CostMeasurements currentMeasurements = entry.getValue();
                    
                    List<String> recordPrefix = new LinkedList<>();
                    recordPrefix.add(entry.getKey().getName());
                    recordPrefix.add(currentMeasurements.measuredFlowID);

                    for (CostMeasurements.Measurement singleMeasurement : currentMeasurements.trackedMeasurements) {
                        List<String> currentRecord = new LinkedList<>(recordPrefix);
                        currentRecord.addAll(singleMeasurement.values());
                        csvPrinter.printRecord(currentRecord);
                    }
                }
            }
            logger.info("Cost measurements written to " + operationCostsFilePath.toAbsolutePath().toString());
        } catch (IOException ex) {
            System.err.println(ex.toString());
            logger.error(ex.toString());
        }

        try (
            BufferedWriter writer = Files.newBufferedWriter(aggregatedCostsFilePath);
            CSVPrinter csvPrinter = new CSVPrinter(writer, aggregatedCostsCSV);
        ) {
            final List<FlowFeature> orderedFields = Arrays.stream(
                this.aggregatedCostsCSV.getHeader()
            ).map(FlowFeature::getByName)
            .collect(Collectors.toList());

            Iterator<String> flowIDTracker = flowIDs.iterator();
            List<String> record = new LinkedList<>();
            
            for (Map<FlowFeature, CostMeasurements> flowMeasurements : this.measurements) {
                record.add(flowIDTracker.next());

                for (FlowFeature featureToAggregate : orderedFields) {
                    CostMeasurements associatedCostMeasurements = flowMeasurements.get(featureToAggregate);
                    if (associatedCostMeasurements == null) {
                        continue;
                    }

                    Double summedCost = 0.;

                    for (CostMeasurements.Measurement individualMeasurement : associatedCostMeasurements.trackedMeasurements) {
                        // TODO: do so for memory used later.
                        summedCost += individualMeasurement.duration;
                    }
                    record.add(summedCost.toString());
                }

                csvPrinter.printRecord(record);
                
                record.clear();
            }

            logger.info("Cost measurements written to " + aggregatedCostsFilePath.toAbsolutePath().toString());
        } catch (IOException ex) {
            System.err.println(ex.toString());
            logger.error(ex.toString());
        }
    }
}
