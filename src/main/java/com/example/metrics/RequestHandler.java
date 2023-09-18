package com.example.metrics;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import java.util.Random;

public class RequestHandler implements Handler<HttpServerRequest> {

  private final WebClient webClient;
  private final Random rand = new Random();

  public RequestHandler(Vertx vertx) {
    this.webClient = WebClient.create(vertx, new WebClientOptions()
        .setDefaultHost("localhost")
        .setDefaultPort(8889));
  }

  @Override
  public void handle(HttpServerRequest request) {
    doWork()
        .onFailure(e -> request.response().setStatusCode(500).end(e.getMessage()))
        .onSuccess(x -> request.response().end("Success!\n"));
  }

  private Future<Void> doWork() {
    return webClient
        .get("/sleep")
        .setQueryParam("ms", String.valueOf(rand.nextInt(10, 1000)))
        .send()
        .mapEmpty();
  }
}
