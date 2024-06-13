package io.quarkiverse.businessscore.scheduler.deployment;

import io.quarkiverse.businessscore.scheduler.runtime.BusinessScoreScheduler;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;

class BusinessScoreSchedulerProcessor {

    @BuildStep
    AdditionalBeanBuildItem beans() {
        return AdditionalBeanBuildItem.unremovableOf(BusinessScoreScheduler.class);
    }

}
