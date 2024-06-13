package io.quarkiverse.businessscore.deployment;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkiverse.businessscore.Scored;
import io.quarkiverse.businessscore.runtime.ScoredInterceptor;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

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

}
