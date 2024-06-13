package io.quarkiverse.businessscore.runtime;

import java.time.Duration;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.business-score")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface BusinessScoreConfig {

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
     * The limit at which the extension will remove all records outside the current time window.
     */
    @WithDefault("1000")
    int autoCompactLimit();

}
