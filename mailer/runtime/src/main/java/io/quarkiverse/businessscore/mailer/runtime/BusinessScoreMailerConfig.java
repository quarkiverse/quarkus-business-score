package io.quarkiverse.businessscore.mailer.runtime;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.business-score.mailer")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface BusinessScoreMailerConfig {

    /**
     * The sender address.
     */
    String from();

    /**
     * The comma-separated list of recipients.
     */
    String to();

    /**
     * The subject. The value is used as a Qute template with available data: {@code appName} and {@code appVersion}.
     */
    @WithDefault("[ZOMBIE DETECTED] {appName}:{appVersion}")
    String subject();

}
