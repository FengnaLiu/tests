package jp.co.cloudace.dataflow.utils;

import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.fs.MatchResult;
import org.apache.beam.sdk.transforms.DoFn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;

public class OpenFileUsingSJIS extends DoFn<FileIO.ReadableFile, String> {
    private static final Logger LOG = Logger.getLogger(OpenFileUsingSJIS.class.getName());

    @ProcessElement
    public void processElement(ProcessContext c) {
        FileIO.ReadableFile rf = c.element();
        // 元ファイルがMS932(=Shift-JIS)でencodingされていると仮定して読み込む
        try (ReadableByteChannel rbc = rf.open();
             InputStream is = Channels.newInputStream(rbc);
             BufferedReader br = new BufferedReader(new InputStreamReader(is, "MS932"))) {
            String line;
            while ((line = br.readLine()) != null) {
                c.output(line);
            }
        } catch (IOException e) {
            LOG.warning(String.format("IOException_1:%s", rf.toString()));
            MatchResult.Metadata md = rf.getMetadata();
            md.resourceId().getFilename();
            LOG.warning(String.format("IOException_2:%s", rf.getMetadata().resourceId().getFilename()));
            e.printStackTrace();
            // ignore and continue
        }
    }
}
