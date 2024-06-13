package io.quarkiverse.businessscore.http.deployment;

import io.quarkiverse.businessscore.http.runtime.BusinessScoreHttp;
import io.quarkiverse.businessscore.http.runtime.BusinessScoreHttpConfig;
import io.quarkiverse.businessscore.http.runtime.BusinessScoreRecorder;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;

class BusinessScoreHttpProcessor {

    @BuildStep
    AdditionalBeanBuildItem beans() {
        return AdditionalBeanBuildItem.unremovableOf(BusinessScoreHttp.class);
    }

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    void defineRoutes(BusinessScoreRecorder recorder, BuildProducer<RouteBuildItem> routes,
            NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem, BusinessScoreHttpConfig config) {

        routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                .management()
                .route(config.rootPath())
                .routeConfigKey("quarkus.business-score.root-path")
                .handler(recorder.createScoreHandler())
                .displayOnNotFoundPage()
                .blockingRoute()
                .build());

        routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                .management()
                .nestedRoute(config.rootPath(), "records")
                .handler(recorder.createScoreRecordsHandler())
                .displayOnNotFoundPage()
                .blockingRoute()
                .build());

    }
}
