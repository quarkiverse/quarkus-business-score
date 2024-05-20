package io.quarkiverse.businessscore.deployment;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkiverse.businessscore.Scored;
import io.quarkiverse.businessscore.runtime.BusinessScoreConfig;
import io.quarkiverse.businessscore.runtime.BusinessScoreRecorder;
import io.quarkiverse.businessscore.runtime.ScoredInterceptor;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;

class BusinessScoreProcessor {

    private static final String FEATURE = "business-score";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem beans() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClass("io.quarkiverse.businessscore.runtime.BusinessScoreImpl")
                .addBeanClasses(BusinessScore.class, Scored.class, ScoredInterceptor.class).build();
    }

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    void defineRoutes(BusinessScoreRecorder recorder, BuildProducer<RouteBuildItem> routes,
            NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem, BusinessScoreConfig config) {

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
