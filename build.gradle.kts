import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.4.5"
val junitJupiterVersion = "5.9.1"

val reproducerMainVerticle = "com.example.metrics.MainVerticle"
val reproducerClassName = "com.example.metrics.Requester"
val listenerClassName = "com.example.metrics.Listener"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(reproducerClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-micrometer-metrics")
  implementation("io.vertx:vertx-web-client")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to reproducerMainVerticle))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", reproducerMainVerticle, "--redeploy=$watchForChange", "--launcher-class=$reproducerClassName", "--on-redeploy=$doOnChange")
}

tasks.register("runListener", JavaExec::class) {
  group = "Execution"
  description = "Run the listener server"
  classpath = sourceSets.main.get().runtimeClasspath
  mainClass.set(listenerClassName)
}
