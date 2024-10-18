package io.quarkiverse.businessscore.selfcheck.runtime;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.business-score.self-check")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface BusinessScoreSelfCheckConfig {

    String DEFAULT_CRON = "0 0 0/6 * * ?";

    /**
     * The CRON trigger used to schedule an automatic self-check.
     * <p>
     * If {@link BusinessScore.ZombieStatus#isZombie()} returns {@code true} then a warning message is logged and a CDI event of
     * type {@link BusinessScore.ZombieStatus} is fired synchronously.
     */
    @WithDefault(DEFAULT_CRON)
    String cron();

    /**
     * If set to {@code true} then the shutdown process is initiated when {@link BusinessScore.ZombieStatus#isZombie()} returns
     * {@code true} during the self-check.
     */
    @WithDefault("false")
    boolean autoExit();

}
