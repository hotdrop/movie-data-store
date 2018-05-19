# MovieDataStore
アプリで表示する映画情報を保管しておくためのサーバーアプリケーションです。  
これ自体は単にデータの保存とその出し入れ用APIを提供するのみです。  
もともとアプリから映画情報のAPIを直接叩く予定でしたが、マッチするものが見つかりませんでした。  
そのため、自分で手動入力するなりした情報を保存するためのハコを作り、そこから取り出してアプリに流そうと考え作成するに至りました。

# 構成
  - kotlin: 1.2.41
  - SpringBoot: 1.5.7.RELEASE
  - Redis
  - springfox-swagger: テストで使用

# 起動方法
IntelliJから直接起動する方法と`docker compose`で起動する方法を書きます。
デバッグしたい場合はIntelliJから、単にAPI実行のために使いたい場合は`docker compose`で起動するのが良いかと思います。
あといずれの手順もappのカレントディレクトリにいる想定です。

## IntelliJでSpringBootを直接起動する場合
  1. データ保存先ディレクトリの作成
    Redisはdockerコンテナで起動します。その際、保存したデータを残しておくためdataディレクトリをホスト側のディレクトリにマウントします。
    そのディレクトリを適当に決めておきます。
  2. Springboot起動
    ```
    ./myBuild.sh bootRun
    ```
    このコマンドで`application.yml`をローカル用にします。Redisのホスト名を変更するのみです。
    理由はSpringbootとRedisを別々のコンテナとしているため、IntelliJIdeaから起動するパターンとdocker-composeで両方コンテナとして起動する場合で分ける必要があるためです。いちいち手動で書き直すのが面倒だったし他にもjarファイルの配置などがあったという理由でmyBuild.shを作りました。
  3. Redis手動起動
    ポート番号は2596適当です。これである必要はありません。 　
    変更したい場合は`src/main/resources/application.yml`のポート番号を修正します。
    ```
    docker run -v [1で決めたディレクトリパス]:/data -it --rm -p 2596:2596 redis /bin/bash
    redis-server --port 2596 &
    redis-cli -p 2596
    ```

## docker composeで起動
  1. データ保存先ディレクトリの作成
    直接起動の1と同じですが、ディレクトリを決めたらmyBuild.shも修正します。deployの方です。
    デフォルトではdocker-composeを実行する場所から`../../dockerShare/movie-store`を作業ディレクトリとしています。
    Redisのデータの他にMovieDataStore（このサーバーアプリケーション）のjarファイルも次の手順で配置されます。
  2. デプロイ
    ```
    ./myBuild.sh deploy
    ```
  3. 実行
    ```
    cd docker
    docker-compose up
    ```

# SwaggerUIでのAPIテスト
  アプリケーション起動後に以下のURLでひらけます。application.ymlのポート番号を変更した場合はそれに合わせます。
  https://localhost:7463/swagger-ui.html

# その他
  - application.ymlに書いてあるhttpは削ったほうがいい
  - 画像イメージをどこに置くか検討中
