package io.quarkiverse.businessscore.runtime;

import java.time.Duration;

import io.quarkiverse.businessscore.BusinessScore;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.business-score")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface BusinessScoreConfig {

    /**
     * The root path for management endpoints.
     * <p>
     * By default, this value is resolved as a path relative to {@code quarkus.http.non-application-root-path}. If the
     * management interface is enabled, the value will be resolved as a path relative to
     * {@code quarkus.management.root-path}.
     */
    @WithDefault("business-score")
    String rootPath();

    /**
     * The point at which the application is considered a zombie.
     * <p>
     * An application is considered a zombie if the current score is below the threshold in the current time window.
     */
    @WithDefault("10")
    long zombieThreshold();

    /**
     * The amount of time a score record is kept alive.
     * <p>
     * By default, it is kept for one week.
     */
    @WithDefault("7D")
    Duration timeWindow();

    /**
     * The interval after which the application tests itself and if {@link BusinessScore.ZombieStatus#isZombie()} returns
     * {@code true} then logs a warning and fires a CDI event of type {@link BusinessScore.ZombieStatus} synchronously.
     */
    @WithDefault("6H")
    Duration zombieCheckInterval();
}
