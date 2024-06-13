package io.quarkiverse.businessscore.scheduler.runtime;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.business-score.scheduler")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface BusinessScoreSchedulerConfig {

    /**
     * The CRON trigger used to schedule an automatic self-test.
     * <p>
     * If {@link BusinessScore.ZombieStatus#isZombie()} returns {@code true} then a warning message is logged and a CDI event of
     * type {@link BusinessScore.ZombieStatus} is fired synchronously.
     */
    @WithDefault("0 0 0/6 * * ?")
    String cron();

}
