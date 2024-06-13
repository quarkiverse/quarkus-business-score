package io.quarkiverse.businessscore.health.deployment;

import io.quarkiverse.businessscore.health.runtime.BusinessScoreHealthCheck;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;

class BusinessScoreHealthProcessor {

    @BuildStep
    AdditionalBeanBuildItem beans() {
        return AdditionalBeanBuildItem.unremovableOf(BusinessScoreHealthCheck.class);
    }
}
