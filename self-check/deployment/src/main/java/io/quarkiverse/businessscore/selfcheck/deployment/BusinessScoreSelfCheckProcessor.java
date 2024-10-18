package io.quarkiverse.businessscore.selfcheck.deployment;

import io.quarkiverse.businessscore.selfcheck.runtime.BusinessScoreSelfCheck;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;

class BusinessScoreSelfCheckProcessor {

    @BuildStep
    AdditionalBeanBuildItem beans() {
        return AdditionalBeanBuildItem.unremovableOf(BusinessScoreSelfCheck.class);
    }

}
