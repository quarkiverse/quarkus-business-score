package io.quarkiverse.businessscore.mailer.deployment;

import io.quarkiverse.businessscore.mailer.runtime.BusinessScoreMailer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;

public class BusinessScoreMailerProcessor {

    @BuildStep
    AdditionalBeanBuildItem beans() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(BusinessScoreMailer.class, BusinessScoreMailer.BusinessScoreMessage.class)
                .setUnremovable()
                .build();
    }

}
