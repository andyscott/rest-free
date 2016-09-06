//

lazy val root = (project in file(".")).aggregate(
  common,
  `service-user`
).settings(unidocSettings: _*)

lazy val akkaVersion       = "2.4.9"
lazy val catsVersion       = "0.7.0"
lazy val shapelessVersion  = "2.3.2"
lazy val refinedVersion    = "0.5.0"
lazy val scalacheckVersion = "1.13.2"
lazy val quillVersion      = "0.9.0"
lazy val monixVersion      = "2.0.0"

lazy val common = (project in file("common"))
  .settings(name := "common")
  .settings(libraryDependencies ++=
    Seq(
      "org.typelevel"     %% "cats-core"              % catsVersion,
      "org.typelevel"     %% "cats-free"              % catsVersion,
      "com.chuusai"       %% "shapeless"              % shapelessVersion,
      "eu.timepit"        %% "refined"                % refinedVersion,
      "com.typesafe.akka" %% "akka-actor"             % akkaVersion,
      "io.getquill"       %% "quill-cassandra"        % quillVersion,
      "io.monix"          %% "monix"                  % monixVersion,
      "io.monix"          %% "monix-cats"             % monixVersion,
      "io.monix"          %% "monix-eval"             % monixVersion
    )
  )

lazy val `service-user` = (project in file("service-user"))
  .settings(name := "service-uesr")
  .dependsOn(common)

lazy val `service-user-impl` = (project in file("service-user-impl"))
  .settings(name := "service-user-impl")
  .dependsOn(`service-user`)

lazy val `rain-demo` = (project in file("rain-demo"))
  .settings(name := "rain-demo")
  .dependsOn(`service-user-impl`)
