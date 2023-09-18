package com.example.metrics;

import io.vertx.core.Vertx;

public class Listener {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    vertx.deployVerticle(new ListenerVerticle());
  }
}
