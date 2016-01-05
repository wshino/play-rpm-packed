# play アプリケーションを rpm でデプロイする

## モチベーション

環境的に多段 ssh をしなければならなかったり、起動したり止めたりのスクリプトを作るのが面倒だった。
daemon 化したほうがインフラ管轄の方達も楽そうだった。
sonatype nexus で rpm を管理できることを知った。


*OSX で試しましたよ*


## rpm を作る

sbt, activator, rpm を入れる

```
brew install sbt typesafe-activator rpm
```

play-scala テンプレートを選ぶ

```
x> activator new

Fetching the latest list of templates...

Browse the list of templates: http://typesafe.com/activator/templates
Choose from these featured templates or enter a template name:
  1) minimal-akka-java-seed
  2) minimal-akka-scala-seed
  3) minimal-java
  4) minimal-scala
  5) play-java
  6) play-scala
(hit tab to see a list of all templates)
> 6
Enter a name for your application (just press enter for 'play-scala')
> rpm-pack
```

build.sbt を編集する

RpmPlugin を記述

```
lazy val root = (project in file(".")).enablePlugins(PlayScala, RpmPlugin)
```

version にはハイフンが使えないので、
ハイフンを含まない version 記述にするか rpm 用の version を指定してあげる

```
version := "1.0"
```

or

```
version in Rpm := "1.0"
```

vendor, group, license, package 説明 が必要

```
rpmVendor := "wshino"

rpmGroup := Some("wshino")

rpmLicense := Some("BSD")

packageDescription in Linux := "rpm-pack"

```

OSX の場合は下記の記述が必要

```
rpmBrpJavaRepackJars := true
```

sbt コマンドを実行

```
x> sbt rpm:packageBin
```

エラーはでるけど rpm ができましたよ

## sonatype nexus に登録する

sonatype nexus の導入と rpm repository の作り方は割愛

https://books.sonatype.com/nexus-book/reference/yum.html

## build.sbt

RpmDeployPlugin を enable にする

```
lazy val root = (project in file("."))
  .enablePlugins(PlayScala, RpmPlugin, RpmDeployPlugin)
```

認証情報と配信先の rpm repository を記述する

build.sbt に書きたくない場合は、別ファイルにしておく。credentials の host 名を書くところは port 番号はいらないです

http://www.scala-sbt.org/0.13/docs/Publishing.html#Credentials

```
credentials += Credentials("Sonatype Nexus Repository Manager", "192.168.99.100", "admin", "admin123")

publishTo := {
  val nexus = "http://192.168.99.100:32679/nexus/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/middleware")
  else
    Some("releases"  at nexus + "content/repositories/middleware")
}

```

sbt を実行

```
x> sbt rpm:publish
```

あとは repository を各インスタンスから引けるようにしておけば yum コマンドで導入できるので楽よ

```
# yum install rpm-pack
```
