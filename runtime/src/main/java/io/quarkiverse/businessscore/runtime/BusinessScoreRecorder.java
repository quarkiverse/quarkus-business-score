package io.quarkiverse.businessscore.runtime;

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
                BusinessScoreImpl businessScore = Arc.container().instance(BusinessScoreImpl.class).get();

                HttpServerResponse resp = ctx.response();
                resp.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

                JsonObject json = new JsonObject();
                addApplicationInfo(json, businessScore.applicationConfig);

                json.put("score", businessScore.getCurrent());
                json.put("zombieThreshold", businessScore.getZombieThreshold());
                if (businessScore.config.zombieThreshold() != businessScore.getZombieThreshold()) {
                    json.put("initialZombieThreshold", businessScore.config.zombieThreshold());
                }
                json.put("timeWindow", businessScore.getTimeWindow().toString());
                if (!businessScore.config.timeWindow().equals(businessScore.getTimeWindow())) {
                    json.put("initialTimeWindow", businessScore.config.timeWindow().toString());
                }

                resp.end(json.toBuffer());
            }
        };
    }

    public Handler<RoutingContext> createScoreRecordsHandler() {
        return new Handler<RoutingContext>() {

            @Override
            public void handle(RoutingContext ctx) {
                BusinessScoreImpl businessScore = Arc.container().instance(BusinessScoreImpl.class).get();

                HttpServerResponse resp = ctx.response();
                resp.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

                JsonObject json = new JsonObject();
                addApplicationInfo(json, businessScore.applicationConfig);

                JsonArray recordsJson = new JsonArray();
                for (BusinessScore.Score r : businessScore.getCurrentRecords()) {
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
