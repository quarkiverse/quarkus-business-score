package io.quarkiverse.businessscore.http.runtime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkus.arc.Arc;
import io.quarkus.runtime.ApplicationConfig;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class BusinessScoreRecorder {

    public Handler<RoutingContext> createScoreHandler() {
        return new Handler<RoutingContext>() {

            @Override
            public void handle(RoutingContext ctx) {
                BusinessScoreHttp http = Arc.container().instance(BusinessScoreHttp.class).get();

                HttpServerResponse resp = ctx.response();
                resp.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

                JsonObject json = new JsonObject();
                addApplicationInfo(json, http.appConfig);

                BusinessScore.ZombieStatus status = http.score.test();
                json.put("zombie", status.isZombie());
                json.put("score", status.score());
                json.put("zombieThreshold", status.threshold());
                if (http.businessScoreConfig.zombieThreshold() != status.threshold()) {
                    json.put("initialZombieThreshold", http.businessScoreConfig.zombieThreshold());
                }
                json.put("timeWindow", status.timeWindow().toString());
                if (!http.businessScoreConfig.timeWindow().equals(status.timeWindow())) {
                    json.put("initialTimeWindow", http.businessScoreConfig.timeWindow().toString());
                }

                resp.end(json.toBuffer());
            }
        };
    }

    public Handler<RoutingContext> createScoreRecordsHandler() {
        return new Handler<RoutingContext>() {

            @Override
            public void handle(RoutingContext ctx) {
                BusinessScoreHttp http = Arc.container().instance(BusinessScoreHttp.class).get();

                HttpServerResponse resp = ctx.response();
                resp.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

                JsonObject json = new JsonObject();
                addApplicationInfo(json, http.appConfig);

                JsonArray recordsJson = new JsonArray();
                for (BusinessScore.Score r : http.score.getCurrentRecords()) {
                    JsonObject recordJson = new JsonObject();
                    recordJson.put("value", r.value());
                    recordJson.put("timestamp", Instant.ofEpochMilli(r.timestamp()).toString());
                    Set<String> labels = r.labels();
                    if (!labels.isEmpty()) {
                        JsonArray labelsJson = new JsonArray();
                        for (String label : labels) {
                            labelsJson.add(label);
                        }
                        recordJson.put("labels", labelsJson);
                    }
                    recordsJson.add(recordJson);
                }
                json.put("records", recordsJson);

                resp.end(json.toBuffer());
            }
        };
    }

    private static void addApplicationInfo(JsonObject json, ApplicationConfig applicationConfig) {
        json.put("serverTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        JsonObject appJson = new JsonObject();
        appJson.put("name", applicationConfig.name.orElse("n/a"));
        appJson.put("version", applicationConfig.version.orElse("n/a"));
        json.put("application", appJson);
    }

}
