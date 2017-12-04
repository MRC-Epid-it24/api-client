/*
This file is part of Intake24.

Copyright 2015, 2016, 2017 Newcastle University.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import org.scalajs.jsenv.nodejs.NodeJSEnv

name := "api-client"

version := "1.0.0-SNAPSHOT"

organization := "uk.ac.ncl.openlab.uk.ac.ncl.openlab.intake24.api.client.api.client.test"

description := "Intake24 cross-platform API client types"



lazy val apiShared = crossProject.in(file("api-shared")).settings(
  scalaVersion := "2.12.4"
)
lazy val apiSharedJS = apiShared.js
lazy val apiSharedJVM = apiShared.jvm

lazy val apiClient = crossProject.in(file(".")).dependsOn(apiShared).settings(
  scalaVersion := "2.12.4",
  libraryDependencies ++= Seq(
    "fr.hmil" %%% "roshttp" % "2.0.2",
    "io.circe" %%% "circe-core" % "0.8.0",
    "io.circe" %%% "circe-generic" % "0.8.0",
    "io.circe" %%% "circe-parser" % "0.8.0",
    "org.typelevel" %%% "cats-core" % "0.9.0",
    "org.scalatest" %%% "scalatest" % "3.0.4" % "test"
  )
).jvmSettings(
  libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.7.25",
    "org.rogach" %% "scallop" % "2.0.5",
    "commons-io" % "commons-io" % "2.5",
    "com.opencsv" % "opencsv" % "3.9"
  )
).jsSettings(
  jsEnv := new NodeJSEnv(NodeJSEnv.Config()
    .withExecutable("/usr/local/bin/node")
    .withEnv(Map(
      "INTAKE24_API_TEST_URL" -> "http://localhost:9001",
      "INTAKE24_API_TEST_USER" -> "intake-test@di-test.com",
      "INTAKE24_API_TEST_PASSWORD" -> "BlahBlah123"
    ))),
    traceLevel.in(Test) := 0
)

lazy val apiClientJVM = apiClient.jvm
lazy val apiClientJS = apiClient.js


// libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"