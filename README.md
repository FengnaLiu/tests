##BigQueryテーブル作成

`$ bq mk --table --time_partitioning_field processing_date  liu-fiona:test.data ./BQ.json`

## テンプレート作成
`$ mvn -P dataflow-runner compile exec:java \
         -Dexec.mainClass=jp.co.cloudace.dataflow.DoFnValueProvider \
         -Dexec.args="--project=liu-fiona \
                      --runner=DataflowRunner \
                      --stagingLocation=gs://liu-fiona/staging \
                      --templateLocation=gs://liu-fiona/templates/DoFnValueProvider" 
    `
## Local実行
`$ mvn -Pdirect-runner compile exec:java -Dexec.mainClass=jp.co.cloudace.dataflow.DoFnValueProvider \
        -Dexec.args="--project=liu-fiona \
                     --inputFile=data.csv \
                     --outputBQPath=liu-fiona:test.data \
                     --customGcsTempLocation=gs://liu-fiona/tmp \
                     --processingDate=2020-10-01" \
    `