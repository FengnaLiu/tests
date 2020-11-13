package jp.co.cloudace.dataflow;
import com.google.api.services.bigquery.model.TableRow;
import jp.co.cloudace.dataflow.utils.CustomOptions;
import jp.co.cloudace.dataflow.utils.OpenFileUsingSJIS;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.Compression;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

public class DoFnValueProvider {
    private static final Logger LOG = Logger.getLogger(DoFnValueProvider.class.getName());
    public static void main(String[] args){
        CustomOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(CustomOptions.class);
        Pipeline p = Pipeline.create(options);
        PCollection<String> p1 = p
                .apply("ReadFromFile", FileIO.match().filepattern(options.getInputFile()))
                .apply(FileIO.readMatches().withCompression(Compression.UNCOMPRESSED))
                .apply(ParDo.of(new OpenFileUsingSJIS()));
        PCollection<TableRow> p2=p1.apply(ParDo.of(new ConvertBQFormat(options.getProcessingDate())));

        p2.apply("WriteToBq",
                BigQueryIO.writeTableRows()
                        .to(options.getOutputBQPath())
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER) // テーブルは作らない
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND) // 追記
                        .withCustomGcsTempLocation(options.getCustomGcsTempLocation()));
        LOG.info("construct pipeline end.");

        p.run();


    }

    static class ConvertBQFormat extends DoFn<String, TableRow> {
        private static final long serialVersionUID = 1L;
        final int EXPECT_COLUMN_LENGTH = 2;
        final String SEPARATE_CHAR = ",";
        ValueProvider<String> PROCESSING_DATE;
        ConvertBQFormat(ValueProvider<String> processing_date){
            this.PROCESSING_DATE=processing_date;
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            String input = c.element();
            TableRow output = new TableRow();
            String[] columns = input.split(SEPARATE_CHAR, -1);
            if (columns.length != EXPECT_COLUMN_LENGTH) {
                LOG.warning(String.format("column count isn't %d but %d. (original data:%s)", EXPECT_COLUMN_LENGTH,
                        columns.length, input));
                return;
            }
            SimpleDateFormat dft = new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss"));
            dft.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date today = new Date();
            String nowStr=dft.format(today);
            try {
                // 日時データを、入力データからBigQueryに投入可能な形式に変換する
                output.set("row",columns[0]);
                output.set("data",columns[1]);
                output.set("processing_date", PROCESSING_DATE);
                output.set("insert_timestamp", nowStr);
                c.output(output);
            } catch (Exception e) {
                e.printStackTrace();
                LOG.warning(String.format("fail to convert, drop it:%s", input));
            }
        }
    }


}
