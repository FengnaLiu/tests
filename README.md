# DoFnValueProvider
ValueProviderで日付のStringを自作DoFnに渡せるかどうかの検証である。

## 検証ステップ
### BigQueryテーブル作成
`$ bq mk --table --time_partitioning_field processing_date  liu-fiona:test.data ./BQ.json`

### テンプレート作成
`$ mvn -P dataflow-runner compile exec:java \
         -Dexec.mainClass=jp.co.cloudace.dataflow.DoFnValueProvider \
         -Dexec.args="--project=liu-fiona \
                      --runner=DataflowRunner \
                      --stagingLocation=gs://liu-fiona/staging \
                      --templateLocation=gs://liu-fiona/templates/DoFnValueProvider" 
    `
### テンプレートアップロード
`$ gsutil cp DoFnValueProvider_metadata gs://liu-fiona/templates/ `

### inputFileアップロード
'$ gsutil cp data.csv gs://liu-fiona/input/'

### templates起動
UIから起動する