name := "RxSolarSystem"

version := "1.0"

scalaVersion := "2.11.5"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies ++= Seq(
   "io.reactivex" % "rxswing" % "0.21.0",
   "io.reactivex" %% "rxscala" % "0.23.0",
   "org.scala-lang" % "scala-swing" % "2.10.4"
)

scalacOptions ++= Seq("-deprecation", "-feature")
