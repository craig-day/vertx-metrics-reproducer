package com.example.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import java.time.Duration;

public class Start {

  public static void main(String[] args) {
    VertxOptions options = new VertxOptions()
        .setMetricsOptions(metricsOptions());

    Vertx vertx = Vertx.vertx(options);

    vertx.deployVerticle(new MainVerticle());
  }

  private static MetricsOptions metricsOptions() {
    // Publish metrics every 5 seconds
    LoggingRegistryConfig config = new LoggingRegistryConfig() {
      @Override
      public Duration step() {
        return Duration.ofSeconds(5);
      }

      @Override
      public String get(String key) {
        return null;
      }
    };

    MeterRegistry registry = new LoggingMeterRegistry(config, Clock.SYSTEM, System.out::println);

    return new MicrometerMetricsOptions()
        .setEnabled(true)
        .setMicrometerRegistry(registry);
  }
}
