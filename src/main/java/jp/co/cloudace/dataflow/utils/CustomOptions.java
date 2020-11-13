package jp.co.cloudace.dataflow.utils;

import org.apache.beam.sdk.extensions.gcp.options.GcpOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.ValueProvider;

public interface CustomOptions extends PipelineOptions {
    @Description("Path of the file to read")
    @Default.String("data.csv")
    ValueProvider<String> getInputFile();
    void setInputFile(ValueProvider<String> value);

    @Description("Processing_date")
    @Default.String("2020-10-01")
    ValueProvider<String> getProcessingDate();
    void setProcessingDate(ValueProvider<String> value);

    @Description("Path of the table to write")
    @Default.String("liu-fiona:test.data")
    ValueProvider<String> getOutputBQPath();
    void setOutputBQPath(ValueProvider<String> value);

    @Description("Path of the GcsTempLocation for using BigQuery")
    ValueProvider<String> getCustomGcsTempLocation();
    void setCustomGcsTempLocation(ValueProvider<String> value);
}