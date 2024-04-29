package dev.sequana.cyber;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.FlowFeature;

public class FlowCostRecorder {
    final public static String TIMESTAMP_FIELD = FlowFeature.tstp.getName();
    final public static String FLOW_ID_FIELD = FlowFeature.fid.getName();
    final public static String OPERATION_FIELD = "Operation";
    final public static String DURATION_NS_FIELD = "Duration (ns)";
    final public static String MEMORY_USED_FIELD = "Memory Used (bytes)";

    private static final Logger logger = LoggerFactory.getLogger(FlowCostRecorder.class);

    private List<FlowCost> records;

    private CSVFormat aggregatedCostsCSV;
    private Path aggregatedCostsFilePath;

    private CSVFormat operationCostsCSV;
    private Path operationCostsFilePath;

    public FlowCostRecorder(String outputDirectory) {
        this.records = new LinkedList<>();

        this.aggregatedCostsCSV = CSVFormat.DEFAULT.builder()
            .setHeader(FlowFeature.class)
            .build();
        this.aggregatedCostsFilePath = Paths.get(
            outputDirectory,
            "aggregated.costs.csv"
        );

        this.operationCostsCSV = CSVFormat.DEFAULT.builder()
            .setHeader(
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

    public void captureFinalCostsFromFlow(BasicFlow flow) {
        this.records.add(new FlowCost(flow));
    }

    public void close() {
        // TODO: maybe a set of flow IDs to verify not duplicates?
        writeToFile(
            aggregatedCostsFilePath,
            aggregatedCostsCSV,
            FlowCost::summarize
        );

        writeToFile(
            operationCostsFilePath,
            operationCostsCSV,
            FlowCost::get
        );
    }

    private <T> void writeToFile(Path filePath, CSVFormat csvFormat, BiFunction<FlowCost, String, T> fieldGetter) {
        try (
            BufferedWriter writer = Files.newBufferedWriter(filePath);
            CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);
        ) {
            final String[] headerFields = this.aggregatedCostsCSV.getHeader();

            for(FlowCost record: this.records) {
                csvPrinter.printRecord(
                    Arrays.asList(headerFields)
                    .stream()
                    .map(field -> fieldGetter.apply(record, field).toString())
                    .collect(Collectors.toList())
                );
            }

            logger.info("Cost measurements written to " + filePath.toAbsolutePath().toString());
        } catch (IOException ex) {
            System.err.println(ex.toString());
            logger.error(ex.toString());
        }
    }    
}
