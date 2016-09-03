//

lazy val root = (project in file(".")).aggregate(
  common,
  `service-user`

)

lazy val akkaVersion       = "2.4.9"
lazy val catsVersion       = "0.7.0"
lazy val scalacheckVersion = "1.13.2"


lazy val common = (project in file("common"))
  .settings(name := "common")
  .settings(libraryDependencies ++=
    Seq(
      "org.typelevel"     %% "cats-core"              % catsVersion,
      "org.typelevel"     %% "cats-free"              % catsVersion,
      "com.typesafe.akka" %% "akka-actor"             % akkaVersion
    )
  )

lazy val `service-user` = (project in file("service-user"))
  .settings(name := "service-uesr")
  .dependsOn(common)
