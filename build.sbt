name := "Dotty Cats"

version := "1.0"

scalaVersion := "2.12.1"
val monocleVersion = "1.4.0"

libraryDependencies ++= Seq(
    "org.typelevel" %% "cats" % "0.8.1",
    "org.scalatest" %% "scalatest" % "3.0.0",
    "com.github.julien-truffaut" %%  "monocle-core"  % monocleVersion,
    "com.github.julien-truffaut" %%  "monocle-macro" % monocleVersion,
    "com.github.julien-truffaut" %%  "monocle-law"   % monocleVersion % "test"
)

