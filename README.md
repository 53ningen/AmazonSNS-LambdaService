# AmazonSNS-LambdaService

Amazon SNS と連携する AWS Lambda コード

# 機能

### DeleteEndpoint

* `com.github.gomi.snsservice.DeleteEndpoint::handleRequest`
* Amazon SNS のエンドポイントが無効になったイベントを受け取り、そのエンドポイントを削除する処理を走らせる

# 導入方法

1. `cp ./src/resources/application.conf.template ./src/resources/application.conf`
2. `application.conf` へ Amazon SNS の Region や Credentials を設定する
3. `sbt assembly`
4. 出来上がった JAR を AWS Lambda に登録する
5. Amazon SNS のコンソールからアプリケーションを選択し、エンドポイントの Attributes が変わったときに先ほど作った Lambda が発火するようにフックする	

# ライセンス

MIT
