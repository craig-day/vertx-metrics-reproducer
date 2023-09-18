package com.example.metrics;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.List;

public class ListenerVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);

    router.route("/sleep").handler(this::handleSleep);
    router.route("/").handler(this::handleRequest);

    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(8889, http -> {
          if (http.succeeded()) {
            startPromise.complete();
            System.out.println("HTTP server started on port 8889");
          } else {
            startPromise.fail(http.cause());
          }
        });
  }

  private void handleSleep(RoutingContext ctx) {
    List<String> msParam = ctx.queryParam("ms");

    if (msParam.size() != 1) {
      handleRequest(ctx);
      return;
    }

    String sleepMs = msParam.get(0);
    int millis = Integer.parseInt(sleepMs);

    if (millis <= 0) {
      handleRequest(ctx);
      return;
    }

    vertx.setTimer(millis, id -> ctx.end("Vertx delayed for " + millis + "ms\n"));
  }

  private void handleRequest(RoutingContext ctx) {
    ctx
        .response()
        .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain")
        .end("Hello from vertx\n");
  }
}
